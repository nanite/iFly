package dev.wuffs.ifly.flight;

import dev.wuffs.ifly.blocks.AscensionShardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;

public enum FlightManager {
    INSTANCE;

    // TODO: Fix removing of blocks from the flight blocks.
    private final Map<ResourceKey<Level>, List<BlockPos>> flightBlocks = new HashMap<>();

    // TODO: Technically we don't need to write this whole object as it's trivial to create using the source location.
    private final List<Bounds> flightAreas = new ArrayList<>();

    // Track who we've given flight to so we can tell them to fuck off later
    private final List<UUID> playersWithFlight = new ArrayList<>();

    // TODO: this list should be bounce to a volume so they can only fly in volumes they have access to.
    private final List<UUID> allowedToFly = new ArrayList<>();

    private final List<BlockPos> sourceVolumes = new ArrayList<>();

    // TODO: Use or yeet
    private Map<UUID, Player> playerLookupCache = new HashMap<>();

    // Need some kinda way of binding the source volume to the flight areas

    FlightManager() {

    }

    /**
     *
     * @param server
     */
    public void tick(MinecraftServer server) {
        // TL;DR: Allowed to fly? If no, no fly
        validateSourceVolumes(server);
        ensureFlight(server);
    }

    private void validateSourceVolumes(MinecraftServer server) {
        for (BlockPos sourceVolume : this.sourceVolumes) {
            // SHIT
        }
    }

    private void ensureFlight(MinecraftServer server) {
        var players = server.getPlayerList().getPlayers();

        for (var player : players) {
            var uuid = player.getUUID();

            var allowedToFly = this.allowedToFly.contains(uuid);
            if (!allowedToFly) {
                return;
            }

            var wasInVolume = false;
            for (var volume : this.flightAreas) {
                if (volume.isWithin(player.blockPosition())) {
                    this.ensureFlightForPlayer(player);
                    wasInVolume = true;
                }
            }

            if (!wasInVolume) {
                this.removeFlightForPlayer(player);
            }
        }
    }

    private void ensureFlightForPlayer(ServerPlayer player) {
        // First! Can they fly? If they can, abort
        if (player.getAbilities().mayfly || player.getAbilities().flying) {
            // This typically means we did not make them fly or we've made them fly and they don't need to have it turned off
            return;
        }

        this.switchFlight(player, true);
    }

    private void removeFlightForPlayer(ServerPlayer player) {
        // First, check if they can fly, if they can't abort early
        if (!player.getAbilities().mayfly) {
            return;
        }

        // Next, can they fly, well, they can, that's how we got here, so let's first check if WE made them fly
        var weMadeFly = this.playersWithFlight.contains(player.getUUID());
        if (!weMadeFly) {
            // DO NOT TURN OFF FLIGHT if someone else made them fly
            return;
        }

        this.switchFlight(player, false);
    }

    private void switchFlight(Player player, boolean on) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (on) {
            this.playersWithFlight.add(player.getUUID());
        } else {
            this.playersWithFlight.remove(player.getUUID());
        }

        player.getAbilities().flying = on;
        player.getAbilities().mayfly = on;
        player.onUpdateAbilities();
    }

    public void handleBlockPlaced(Level level, BlockPos pos, BlockState state) {
        if (state.isAir() || !(state.getBlock() instanceof AscensionShardBlock)) {
            return;
        }

        // Hack
        // TODO: FIX ME
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            this.allowedToFly.add(player.getUUID());
        }

        this.flightBlocks.computeIfAbsent(level.dimension(), k -> new ArrayList<>()).add(pos);
        this.addVolume(pos);
    }

    public void handleBlockBroken(Level level, BlockPos pos, BlockState state) {
        // If it's not our block, we don't care.
        if (state.isAir() || !(state.getBlock() instanceof AscensionShardBlock)) {
            return;
        }

        // If the block wasn't in the list, we don't care
        List<BlockPos> blockPos = this.flightBlocks.get(level.dimension());
        if (blockPos == null || !blockPos.contains(pos)) {
            return;
        }

        // Get the volume
        var volume = this.flightAreas.stream().filter(bounds -> bounds.sourceLocation.equals(pos)).findFirst();
        if (volume.isPresent()) {
            // Get all the players in the volume
            var players = level.getServer().getPlayerList().getPlayers();
            for (var player : players) {
                if (volume.get().isWithin(player.blockPosition())) {
                    this.removeFlightForPlayer(player);
                }
            }
        }

        this.flightBlocks.remove(pos);
        this.removeVolume(pos); // This will try and remove any volumes that are using this block as a source
    }

    /**
     * Haha
     */
    private void addVolume(BlockPos location) {
        this.addVolume(location, 64);
    }

    private void addVolume(BlockPos location, int size) {
        var bounds = Bounds.create(location, location, size);
        this.flightAreas.add(bounds);
        this.sourceVolumes.add(location);
    }

    private void removeVolume(BlockPos sourceLocation) {
        // Find the volume using it's sourceLocation
        var volume = this.flightAreas.stream().filter(bounds -> bounds.sourceLocation.equals(sourceLocation)).findFirst();
        volume.ifPresent(this.flightAreas::remove);

        this.sourceVolumes.remove(sourceLocation);
    }

    public CompoundTag writeToCompound() {
        var compound = new CompoundTag();

        compound.put("flightBlocks", writeCompoundMap(this.flightBlocks, (key) -> StringTag.valueOf(key.location().toString()), t -> writeCompoundList(t, NbtUtils::writeBlockPos)));

        compound.put("flightAreas", writeCompoundList(this.flightAreas, Bounds::writeToCompound));
        compound.put("sourceVolumes", writeCompoundList(this.sourceVolumes, NbtUtils::writeBlockPos));
        compound.put("playersWithFlight", writeCompoundList(this.playersWithFlight, NbtUtils::createUUID));
        compound.put("allowedToFly", writeCompoundList(this.allowedToFly, NbtUtils::createUUID));

        return compound;
    }

    public void readFromCompound(CompoundTag tag) {
        this.flightBlocks.clear();
        this.flightAreas.clear();
        this.sourceVolumes.clear();
        this.playersWithFlight.clear();
        this.allowedToFly.clear();

        this.flightBlocks.putAll(readCompoundMap(tag.getList("flightBlocks", Tag.TAG_COMPOUND),
                t -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("k"))),
                t -> readCompoundList((ListTag) t, NbtUtils::readBlockPos)
        ));

        this.flightAreas.addAll(readCompoundList(tag.getList("flightAreas", Tag.TAG_COMPOUND), Bounds::readFromCompound));
        this.sourceVolumes.addAll(readCompoundList(tag.getList("sourceVolumes", Tag.TAG_COMPOUND), NbtUtils::readBlockPos));
        this.playersWithFlight.addAll(readCompoundList(tag.getList("playersWithFlight", Tag.TAG_INT_ARRAY), NbtUtils::loadUUID));
        this.allowedToFly.addAll(readCompoundList(tag.getList("allowedToFly", Tag.TAG_INT_ARRAY), NbtUtils::loadUUID));
    }

    //#region NBT Utils
    private static <T> Tag writeCompoundList(List<T> list, Function<T, Tag> writer) {
        var listTag = new ListTag();

        for (var item : list) {
            var tag = writer.apply(item);
            listTag.add(tag);
        }

        return listTag;
    }

    private static <T> List<T> readCompoundList(ListTag list, Function<CompoundTag, T> reader) {
        var result = new ArrayList<T>();

        for (var tag : list) {
            var item = reader.apply((CompoundTag) tag);
            result.add(item);
        }

        return result;
    }

    private static <K, V> Tag writeCompoundMap(Map<K, V> map, Function<K, Tag> keyWriter, Function<V, Tag> valueWriter) {
        var listTag = new ListTag();

        for (var entry : map.entrySet()) {
            var compound = new CompoundTag();
            compound.put("k", keyWriter.apply(entry.getKey()));
            compound.put("v", valueWriter.apply(entry.getValue()));
            listTag.add(compound);
        }

        return listTag;
    }

    private static <K, V> Map<K, V> readCompoundMap(ListTag list, Function<Tag, K> keyReader, Function<Tag, V> valueReader) {
        var result = new HashMap<K, V>();

        for (var tag : list) {
            var compound = (CompoundTag) tag;
            var key = keyReader.apply(compound.get("k"));
            var value = valueReader.apply(compound.get("v"));
            result.put(key, value);
        }

        return result;
    }
    //#endregion

    /**
     * Simple 2d bounds set for location checking
     *
     * @param startX
     * @param startZ
     * @param endX
     * @param endZ
     */
    record Bounds(
            BlockPos sourceLocation,
            int startX,
            int startZ,
            int endX,
            int endZ
    ) {
        public static Bounds create(BlockPos sourceLocation, BlockPos startPos, BlockPos endPos) {
            return new Bounds(sourceLocation, startPos.getX(), startPos.getZ(), endPos.getX(), endPos.getZ());
        }

        public static Bounds create(BlockPos sourceLocation, Vec3 startPos, Vec3 endPos) {
            return new Bounds(sourceLocation, (int) startPos.x, (int) startPos.z, (int) endPos.x, (int) endPos.z);
        }

        public static Bounds create(BlockPos sourceLocation, Vec3i startPos, Vec3i endPos) {
            return new Bounds(sourceLocation, startPos.getX(), startPos.getZ(), endPos.getX(), endPos.getZ());
        }

        public static Bounds create(BlockPos sourceLocation, BlockPos pos, int expandBy) {
            return new Bounds(sourceLocation, pos.getX() - expandBy, pos.getZ() - expandBy, pos.getX() + expandBy, pos.getZ() + expandBy);
        }

        /**
         * Wow, such performance, much wow
         *
         * @param pos
         * @return
         */
        public boolean isWithin(BlockPos pos) {
            int givenX = pos.getX();
            int givenZ = pos.getZ();

            return givenX > this.startX && givenX < this.endX && givenZ > this.startZ && givenZ < this.endZ;
        }

        public CompoundTag writeToCompound() {
            var compound = new CompoundTag();

            compound.put("sourceLocation", NbtUtils.writeBlockPos(this.sourceLocation));
            compound.putInt("startX", this.startX);
            compound.putInt("startZ", this.startZ);
            compound.putInt("endX", this.endX);
            compound.putInt("endZ", this.endZ);

            return compound;
        }

        public static Bounds readFromCompound(CompoundTag tag) {
            var sourceLocation = NbtUtils.readBlockPos(tag.getCompound("sourceLocation"));
            var startX = tag.getInt("startX");
            var startZ = tag.getInt("startZ");
            var endX = tag.getInt("endX");
            var endZ = tag.getInt("endZ");

            return new Bounds(sourceLocation, startX, startZ, endX, endZ);
        }
    }
}

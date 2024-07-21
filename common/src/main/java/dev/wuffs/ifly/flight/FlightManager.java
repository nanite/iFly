package dev.wuffs.ifly.flight;

import dev.wuffs.ifly.blocks.AscensionShardBlock;
import dev.wuffs.ifly.integration.ModIntegrations;
import dev.wuffs.ifly.integration.TeamsInterface;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO: We need to actually save this data
public class FlightManager extends SavedData {

    private static final int CHECK_INTERVAL = 20 * 5; // 5 seconds

    private final Map<ResourceKey<Level>, List<BlockPos>> flightBlocks = new HashMap<>();

    private final List<FlightBounds> flightAreas = new ArrayList<>();
    private final List<UUID> playersWithFlight = new ArrayList<>();

    // Not saved data
    private final Object2BooleanMap<UUID> playersKnownByVolumes = new Object2BooleanOpenHashMap<>();

    public static FlightManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                FlightManager::new,
                FlightManager::readFromCompound,
                null
        ), "ifly_flight_manager");
    }

    FlightManager() {
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        return writeToCompound(compoundTag);
    }

    /**
     *
     * @param server
     */
    public void tick(ServerLevel level) {
        // TL;DR: Allowed to fly? If no, no fly
        if (level.getServer().getTickCount() % CHECK_INTERVAL == 0) { // Run every 1 second
            validateSourceVolumes(level);
        }

        ensureFlight(level);
    }

    private void validateSourceVolumes(ServerLevel level) {
        for (Map.Entry<ResourceKey<Level>, List<BlockPos>> blocks : this.flightBlocks.entrySet()) {
            // Check if the block still exists, if it doen't call the remove method to clean things up
            // Try and get the dim
            var dimKey = blocks.getKey();

            for (BlockPos pos : blocks.getValue()) {
                var state = level.getBlockState(pos);
                if (state.isAir() || !(state.getBlock() instanceof AscensionShardBlock)) {
                    this.handleBlockBroken(level, pos, state);
                }
            }
        }
    }

    private void ensureFlight(ServerLevel level) {
        var players = level.getServer().getPlayerList().getPlayers();

        for (var player : players) {
            var wasInVolume = false;
            for (var volume : this.flightAreas) {
                // This will only be looked up if we do not already know the result. This reduces the amount of times
                // we have to walk to though a members list. Doing it this way also allows us to have team support without
                // any extra work.
                var knownToVolume = playersKnownByVolumes.computeIfAbsent(player.getUUID(), uuid -> volume.playerCanFly(player));
                if (!knownToVolume) {
                    continue;
                }

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
        // TODO: Attributes
        if (player.getAbilities().mayfly || player.getAbilities().flying) {
            // This typically means we did not make them fly or we've made them fly and they don't need to have it turned off
            return;
        }

        this.switchFlight(player, true);
    }

    // TODO: Attribnutes
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

    /**
     * TODO: Attributes
     * @param player
     * @param on
     */
    private void switchFlight(Player player, boolean on) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (on) {
            this.playersWithFlight.add(player.getUUID());
        } else {
            this.playersWithFlight.remove(player.getUUID());
        }

        this.setDirty();

        player.getAbilities().flying = on;
        player.getAbilities().mayfly = on;
        player.onUpdateAbilities();
    }

    public void handleBlockPlaced(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.isAir() || !(state.getBlock() instanceof AscensionShardBlock)) {
            return;
        }

        // Assign the player as the owner of this block, then add them to the fly list

        this.flightBlocks.computeIfAbsent(level.dimension(), k -> new ArrayList<>()).add(pos);
        this.addVolume(player.getUUID(), pos);
    }

    public void handleBlockBroken(Level level, BlockPos pos, BlockState state) {
        // If it's not our block, we don't care.
        if (state.isAir() || !(state.getBlock() instanceof AscensionShardBlock)) {
            return;
        }

        // If the block wasn't in the list, we don't care
        List<BlockPos> blockPosList = this.flightBlocks.get(level.dimension());
        if (blockPosList == null || !blockPosList.contains(pos)) {
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

        blockPosList.remove(pos);
        // Write the blockPos back to the map
        this.flightBlocks.put(level.dimension(), blockPosList);
        this.removeVolume(pos); // This will try and remove any volumes that are using this block as a source

        // Purge the cache so it can be recalculated later
        this.playersKnownByVolumes.clear();
        this.setDirty();
    }

    /**
     * Haha
     */
    private void addVolume(UUID owner, BlockPos location) {
        this.addVolume(owner, location, 64);
    }

    private void addVolume(UUID owner, BlockPos location, int size) {
        var bounds = FlightBounds.create(owner, location, location, size);
        this.flightAreas.add(bounds);
        this.setDirty();
    }

    private void removeVolume(BlockPos sourceLocation) {
        // Find the volume using it's sourceLocation
        var volume = this.flightAreas.stream().filter(bounds -> bounds.sourceLocation.equals(sourceLocation)).findFirst();
        volume.ifPresent(this.flightAreas::remove);
        this.setDirty();
    }

    @Nullable
    public FlightBounds getVolume(BlockPos sourceLocation) {
        return this.flightAreas.stream().filter(bounds -> bounds.sourceLocation.equals(sourceLocation)).findFirst().orElse(null);
    }

    public CompoundTag writeToCompound(CompoundTag compound) {
        compound.put("flightBlocks", writeCompoundMap(this.flightBlocks, (key) -> StringTag.valueOf(key.location().toString()), t -> writeCompoundList(t, pos -> {
            var compoundTag = new CompoundTag();
            compoundTag.put("blockPos", NbtUtils.writeBlockPos(pos));
            return compoundTag;
        })));

        compound.put("flightAreas", writeCompoundList(this.flightAreas, FlightBounds::writeToCompound));
        compound.put("playersWithFlight", writeCompoundList(this.playersWithFlight, NbtUtils::createUUID));

        return compound;
    }

    public static FlightManager readFromCompound(CompoundTag tag, HolderLookup.Provider provider) {
        var manager = new FlightManager();

        manager.flightBlocks.clear();
        manager.flightAreas.clear();
        manager.playersWithFlight.clear();

        manager.flightBlocks.putAll(readCompoundMap(tag.getList("flightBlocks", Tag.TAG_COMPOUND),
                t -> ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace(tag.getString("k"))),
                t -> readCompoundList((ListTag) t, compoundTag -> NbtUtils.readBlockPos(compoundTag, "blockPos").orElse(BlockPos.ZERO))
        ));

        manager.flightAreas.addAll(readCompoundList(tag.getList("flightAreas", Tag.TAG_COMPOUND), FlightBounds::readFromCompound));
        manager.playersWithFlight.addAll(readCompoundList(tag.getList("playersWithFlight", Tag.TAG_INT_ARRAY), NbtUtils::loadUUID));

        return manager;
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

    public static void LevelTick(ServerLevel serverLevel) {
        FlightManager.get(serverLevel).tick(serverLevel);
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
    public record FlightBounds(
            BlockPos sourceLocation,
            UUID owner,
            List<FlightAccess> members,
            int startX,
            int startZ,
            int endX,
            int endZ
    ) {
        public static FlightBounds create(UUID owner, BlockPos sourceLocation, BlockPos pos, int expandBy) {
            return new FlightBounds(sourceLocation,
                    owner,
                    new ArrayList<>(),
                    pos.getX() - expandBy,
                    pos.getZ() - expandBy,
                    pos.getX() + expandBy,
                    pos.getZ() + expandBy);
        }

        /**
         * Wow, such performance, much wow
         *
         * @param pos the position to check
         * @return boolean if the position is within the bounds
         */
        public boolean isWithin(BlockPos pos) {
            int givenX = pos.getX();
            int givenZ = pos.getZ();

            return givenX > this.startX && givenX < this.endX && givenZ > this.startZ && givenZ < this.endZ;
        }

        public boolean playerCanFly(Player player) {
            if (this.owner.equals(player.getUUID())) {
                return true;
            }

            // Check if the player is a member
            if (this.members.isEmpty()) {
                return false;
            }

            return this.members.stream().anyMatch(member -> member.canFly(player));
        }

        public boolean playerCanManage(Player player) {
            if (this.owner.equals(player.getUUID())) {
                return true;
            }

            // Check if the player is a member
            if (this.members.isEmpty()) {
                return false;
            }

            return this.members.stream().anyMatch(member -> member.canManage(player));
        }

        public CompoundTag writeToCompound() {
            var compound = new CompoundTag();

            var blockPos = new CompoundTag();
            blockPos.put("blockPos", NbtUtils.writeBlockPos(this.sourceLocation));
            compound.put("sourceLocation", blockPos);
            compound.put("owner", NbtUtils.createUUID(this.owner));
            compound.put("members", writeCompoundList(this.members, (member) -> {
                if (member instanceof FlightTeam) {
                    CompoundTag compoundTag = ((FlightTeam) member).writeToCompound();
                    compoundTag.putString("type", "team");
                    return compoundTag;
                }

                CompoundTag compoundTag = ((FlightMembers) member).writeToCompound();
                compoundTag.putString("type", "member");
                return compoundTag;
            }));
            compound.putInt("startX", this.startX);
            compound.putInt("startZ", this.startZ);
            compound.putInt("endX", this.endX);
            compound.putInt("endZ", this.endZ);

            return compound;
        }

        public static FlightBounds readFromCompound(CompoundTag tag) {
            var sourceLocation = NbtUtils.readBlockPos(tag.getCompound("sourceLocation"), "blockPos").orElse(BlockPos.ZERO);
            var owner = NbtUtils.loadUUID(tag.get("owner"));

            var members = readCompoundList(tag.getList("members", Tag.TAG_COMPOUND), (t) -> {
                var type = t.getString("type");
                if (type.equals("team")) {
                    return FlightTeam.readFromCompound(t);
                }

                return FlightMembers.readFromCompound(t);
            });

            var startX = tag.getInt("startX");
            var startZ = tag.getInt("startZ");
            var endX = tag.getInt("endX");
            var endZ = tag.getInt("endZ");

            return new FlightBounds(sourceLocation, owner, members, startX, startZ, endX, endZ);
        }
    }

    public enum Role {
        OWNER,
        MANAGER,
        MEMBER;

        public String id() {
            return this.name().toLowerCase();
        }
    }

    public interface FlightAccess {
        boolean canFly(Player player);

        boolean canManage(Player player);
    }

    public static class FlightTeam implements FlightAccess {
        // Lazy lookup
        private final Supplier<TeamsInterface> teamsLookup = () -> ModIntegrations.factory(this.providerId);

        UUID teamId;
        ResourceLocation providerId;

        public FlightTeam(UUID teamId, ResourceLocation providerId) {
            this.teamId = teamId;
            this.providerId = providerId;
        }

        @Override
        public boolean canFly(Player player) {
            TeamsInterface teamsInterface = this.teamsLookup.get();
            if (teamsInterface == null) {
                return false;
            }

            return teamsInterface.isInTeam(player, this.teamId);
        }

        @Override
        public boolean canManage(Player player) {
            TeamsInterface teamsInterface = this.teamsLookup.get();
            if (teamsInterface == null) {
                return false;
            }

            return teamsInterface.isManager(player, this.teamId);
        }

        public CompoundTag writeToCompound() {
            var compound = new CompoundTag();

            compound.put("teamId", NbtUtils.createUUID(this.teamId));
            compound.putString("providerId", this.providerId.toString());

            return compound;
        }

        public static FlightTeam readFromCompound(CompoundTag tag) {
            var teamId = NbtUtils.loadUUID(tag.get("teamId"));
            var providerId = ResourceLocation.withDefaultNamespace(tag.getString("providerId"));

            return new FlightTeam(teamId, providerId);
        }
    }

    record FlightMembers(UUID player, Role role) implements FlightAccess {
        public CompoundTag writeToCompound() {
            var compound = new CompoundTag();

            compound.put("player", NbtUtils.createUUID(this.player));
            compound.putString("role", this.role.id());

            return compound;
        }

        public static FlightMembers readFromCompound(CompoundTag tag) {
            var player = NbtUtils.loadUUID(tag.get("player"));
            var role = Role.valueOf(tag.getString("role").toUpperCase());

            return new FlightMembers(player, role);
        }

        @Override
        public boolean canFly(Player player) {
            return player.getUUID().equals(this.player);
        }

        @Override
        public boolean canManage(Player player) {
            return this.role == Role.MANAGER || this.role == Role.OWNER;
        }
    }
}

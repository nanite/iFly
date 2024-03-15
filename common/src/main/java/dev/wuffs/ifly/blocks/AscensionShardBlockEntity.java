package dev.wuffs.ifly.blocks;

import dev.wuffs.ifly.network.records.StoredPlayers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AscensionShardBlockEntity extends BlockEntity {

    public static final AABB DETECT_BOX = Shapes.block().bounds();// TODO make config and upgrade modules
    public static final double RADIUS = 64D;
    public List<StoredPlayers> storedPlayers = new ArrayList<>();
    public static ObjectSet<UUID> alreadyFlying = new ObjectOpenHashSet<>();
    public static ObjectSet<UUID> weMadeFlying = new ObjectOpenHashSet<>();

    public AscensionShardBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Blocks.ASHARD_BENTITY.get(), blockPos, blockState);
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos blockPos, BlockState state, T t) {
        if (!(t instanceof AscensionShardBlockEntity entity) || level.getGameTime() % 20 != 0 || entity.level == null) {
            return;
        }

        BlockPos pos = entity.worldPosition;
        AABB aabb = DETECT_BOX.move(pos).inflate(RADIUS).setMinY(level.getMinBuildHeight()).setMaxY(level.getMaxBuildHeight());

        List<Player> players = entity.level.getEntitiesOfClass(Player.class, aabb);

        Set<UUID> playerUUIDs = players.stream().map(Entity::getUUID).collect(Collectors.toSet());

        List<Player> nonSelectedPlayers = entity.level.getServer().getPlayerList().getPlayers().stream()
                .filter(p -> !playerUUIDs.contains(p.getUUID()))
                .collect(Collectors.toList());

        for (Player player : players) {
            if (player == null || player.isCreative() || player.isSpectator()) {
                continue;
            }

            boolean containsIflyTag = player.getTags().contains("ifly:" + blockPos.toShortString());


            // Check if the player is contained in the storedPlayers list
            boolean wfContains = weMadeFlying.contains(player.getUUID());
            boolean afContains = alreadyFlying.contains(player.getUUID());

            boolean isPlayerOwner = entity.storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(player.getUUID()) && storedPlayer.level().isOwner());
            if (!isPlayerOwner && !entity.storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(player.getUUID()))) {
                if(wfContains && !afContains && containsIflyTag){
                    boolean wasFlying = player.getAbilities().flying;
                    weMadeFlying.add(player.getUUID());
                    player.removeTag("ifly:" + blockPos.toShortString());
                    player.getAbilities().flying = false;
                    player.getAbilities().mayfly = false;
                    player.onUpdateAbilities();
                    double distanceToGround = getDistanceToGround(player);
                    if (distanceToGround >= 4 && wasFlying) {
                        int timeToFall = fallTimeCalc((int) Math.ceil(distanceToGround));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, timeToFall));
                    }
                    player.onUpdateAbilities();
                }
                continue;
            }

            if (player.getAbilities().mayfly && !wfContains) {
                alreadyFlying.add(player.getUUID());
            } else if (!player.getAbilities().mayfly && wfContains) {
                alreadyFlying.remove(player.getUUID());
                weMadeFlying.remove(player.getUUID());
            }

            if (!wfContains && !afContains) {
                weMadeFlying.add(player.getUUID());
                player.addTag("ifly:" + blockPos.toShortString());
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        }

        for (Player player : nonSelectedPlayers) {
            boolean containsIflyTag = player.getTags().contains("ifly:" + blockPos.toShortString());
            if (alreadyFlying.contains(player.getUUID()) || !weMadeFlying.contains(player.getUUID()) || player.isCreative() || player.isSpectator() || !containsIflyTag) {
                continue;
            }

            weMadeFlying.remove(player.getUUID());
            boolean wasFlying = player.getAbilities().flying;
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.removeTag("ifly:" + blockPos.toShortString());
            double distanceToGround = getDistanceToGround(player);
            if (distanceToGround >= 4 && wasFlying) {
                int timeToFall = fallTimeCalc((int) Math.ceil(distanceToGround));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, timeToFall));
            }
            player.onUpdateAbilities();
        }
    }


    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        Tag storePlayersCompound = StoredPlayers.LIST_CODEC.encodeStart(NbtOps.INSTANCE, storedPlayers).getOrThrow(false, RuntimeException::new);
        compoundTag.put("storedPlayers", storePlayersCompound);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        storedPlayers = new ArrayList<>(StoredPlayers.LIST_CODEC.parse(NbtOps.INSTANCE, compoundTag.get("storedPlayers")).getOrThrow(false, RuntimeException::new));
    }

    public static double getDistanceToGround(Player player) {
        Vec3 playerLocation = player.position();
        double playerYCoordinate = playerLocation.y;
        BlockPos blockPos = new BlockPos(Mth.floor(playerLocation.x), Mth.floor(playerLocation.y), Mth.floor(playerLocation.z));
        double groundYCoordinate = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos).getY();

        return playerYCoordinate - groundYCoordinate;
    }

    public static int fallTimeCalc(int fallDistance) {
        int initialBlocks = 7;
        int initialTicks = 40;

        int ticksPerBlock = initialTicks / initialBlocks;

        int resultTicks = ticksPerBlock * fallDistance;
//        System.out.println("It would take " + resultTicks + " ticks to fall " + fallDistance + " blocks with the slow fall effect.");
        return resultTicks;
    }
}

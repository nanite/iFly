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
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AscensionShardBlockEntity extends BlockEntity {

    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AscensionShardBlockEntity.class);
    public static boolean ENABLED = true; // TODO REMOVE THIS
    public static final AABB DETECT_BOX = Shapes.block().bounds();// TODO Change to 2d checking on server tick
    public static final double RADIUS = 64D;
    public List<StoredPlayers> storedPlayers = new ArrayList<>();
    public static ObjectSet<UUID> alreadyFlying = new ObjectOpenHashSet<>();
    public static ObjectSet<UUID> weMadeFlying = new ObjectOpenHashSet<>();

    public AscensionShardBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Blocks.ASHARD_BENTITY.get(), blockPos, blockState);
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos blockPos, BlockState state, T t) {
        // TODO REMOVE THIS AFTER TESTING!!!!!!
        if (!ENABLED) {
            return;
        }
        if (!(t instanceof AscensionShardBlockEntity entity) || level.getGameTime() % 20 != 0 || entity.level == null) {
            return;
        }

        BlockPos pos = entity.worldPosition;
        AABB aabb = DETECT_BOX.move(pos).inflate(RADIUS).setMinY(level.getMinBuildHeight()).setMaxY(level.getMaxBuildHeight());
        List<Player> players = entity.level.getEntitiesOfClass(Player.class, aabb);
        Set<UUID> playerUUIDs = players.stream().map(Entity::getUUID).collect(Collectors.toSet());

        // List of server players that are not in the AABB
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
                    setFlight(player, blockPos, false);
                }
            }

//            LOGGER.debug("Player: " + player.getDisplayName().getString() + " mayfly: " + player.getAbilities().mayfly + " flying: " + player.getAbilities().flying);
            if (player.getAbilities().mayfly && !wfContains) {
                alreadyFlying.add(player.getUUID());
            } else if (!player.getAbilities().mayfly) {
                alreadyFlying.remove(player.getUUID());
                weMadeFlying.remove(player.getUUID());
            }

            if (!wfContains && !afContains && entity.storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(player.getUUID()))) {
                setFlight(player, blockPos, true);
            }
//            LOGGER.debug("We made flying: " + weMadeFlying);
//            LOGGER.debug("Already flying: " + alreadyFlying);
        }

        for (Player player : nonSelectedPlayers) {
            boolean containsIflyTag = player.getTags().contains("ifly:" + blockPos.toShortString());
            /*
            * If the player is in creative or spectator mode, we don't care about them
            * If the player doesn't have the ifly tag, we don't care about them
            * If the player is NOT in the weMadeFlying list, we don't care about them
            * If the player is in the alreadyFlying list, we don't care about them
            * */
            if (player.isCreative() || player.isSpectator() || !containsIflyTag || !weMadeFlying.contains(player.getUUID()) || alreadyFlying.contains(player.getUUID())) {
                continue;
            }
            // Remove flight from the player
            setFlight(player, blockPos, false);
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

    public static void setFlight(Player player, BlockPos blockPos, boolean allowFlight) {
        if (allowFlight) {
            /*
            * Redundant checks for the player's game mode and if they are already flying
            * */
            // We don't care about creative or spectator players
            if(player.isCreative() || player.isSpectator()) return;
            // Ignore the player if they are already flying
            if(player.getAbilities().mayfly && !weMadeFlying.contains(player.getUUID()) && !alreadyFlying.contains(player.getUUID())){
                alreadyFlying.add(player.getUUID());
                return;
            }

            player.getAbilities().mayfly = true;
            player.addTag("ifly:" + blockPos.toShortString());

            weMadeFlying.add(player.getUUID());

            player.onUpdateAbilities();
        } else {
            // Ignore the player if they are in creative or spectator mode
            if(player.isCreative() || player.isSpectator()) return;
            // We don't want to remove the players flight abilities if they are set by something else
            if (player.getAbilities().mayfly && alreadyFlying.contains(player.getUUID())) return;

            boolean wasFlying = player.getAbilities().flying;
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.removeTag("ifly:" + blockPos.toShortString());

            weMadeFlying.remove(player.getUUID()); // Remove the player from the weMadeFlying list

            // Calculate the distance to the ground and apply the slow falling effect if the player was flying
            double distanceToGround = getDistanceToGround(player);
            if (distanceToGround >= 4 && wasFlying) {
                int timeToFall = fallTimeCalc((int) Math.ceil(distanceToGround));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, timeToFall));
            }

            player.onUpdateAbilities();
        }
    }
}
package dev.wuffs.ifly.blocks;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TbdBlockEntity extends BlockEntity {

    public static final AABB DETECT_BOX = Shapes.block().bounds();

    public TbdBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Blocks.TBDE.get(), blockPos, blockState);
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos blockPos, BlockState state, T t) {
        if (!(t instanceof TbdBlockEntity entity) || level.getGameTime() % 20 != 0 || entity.level == null) {
            return;
        }

        BlockPos pos = entity.worldPosition;
        double radius = 16D; // TODO make config and upgrade modules
        AABB aabb = DETECT_BOX.move(pos).inflate(radius);

        List<Player> players = entity.level.getEntitiesOfClass(
                Player.class,
                aabb
        );

        Set<UUID> playerUUIDs = players.stream().map(Entity::getUUID).collect(Collectors.toSet());

        List<Player> nonSelectedPlayers = entity.level.getServer().getPlayerList().getPlayers().stream()
                .filter(p -> !playerUUIDs.contains(p.getUUID()))
                .collect(Collectors.toList());

        for (Player player : players) {
            if (player == null || player.isCreative()) {
                continue;
            }
            System.out.println(weMadeFlying);
            System.out.println(alreadyFlying);

            // TODO MAKE SURE THIS IS CORRECT
            if (player.getAbilities().mayfly && !weMadeFlying.contains(player.getUUID())) {
                System.out.println(player.getDisplayName().getString() + " is already flying");
                alreadyFlying.add(player.getUUID());
            } else if (!player.getAbilities().mayfly && weMadeFlying.contains(player.getUUID())) {
                System.out.println("You are removed from flaying");
                alreadyFlying.remove(player.getUUID());
                weMadeFlying.remove(player.getUUID());
            }

            if (!weMadeFlying.contains(player.getUUID()) && !alreadyFlying.contains(player.getUUID())) {
                System.out.println("Your may fly");
                weMadeFlying.add(player.getUUID());
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        }

        for (Player player : nonSelectedPlayers) {
            if (alreadyFlying.contains(player.getUUID()) || !weMadeFlying.contains(player.getUUID()) || player.isCreative()) {
                System.out.println("Player is already flying/creative or we didnt make them fly");
                continue;
            }

            System.out.println("Your not allowed to fly anymore");
            weMadeFlying.remove(player.getUUID());
            boolean wasFlying = player.getAbilities().flying;
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            double distanceToGround = getDistanceToGround(player);
            System.out.println(distanceToGround);
            if (distanceToGround >= 4 && wasFlying) {
                int timeToFall = fallTimeCalc((int) Math.ceil(distanceToGround));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, timeToFall));
            }
            player.onUpdateAbilities();
        }
    }

    static ObjectSet<UUID> alreadyFlying = new ObjectOpenHashSet<>();
    static ObjectSet<UUID> weMadeFlying = new ObjectOpenHashSet<>();

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
        System.out.println("It would take " + resultTicks + " ticks to fall " + fallDistance + " blocks with the slow fall effect.");
        return resultTicks;
    }
}

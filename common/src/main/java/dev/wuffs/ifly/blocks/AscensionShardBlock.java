package dev.wuffs.ifly.blocks;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.util.NetworkHelper;
import dev.wuffs.ifly.flight.FlightManager;
import dev.wuffs.ifly.network.C2SOpenIflyScreen;
import dev.wuffs.ifly.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AscensionShardBlock extends Block implements EntityBlock {

    public AscensionShardBlock() {
        super(Properties.of().strength(0.4f, 3600000.0F).sound(SoundType.STONE).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AscensionShardBlockEntity(blockPos, blockState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (level.isClientSide) {
            super.onRemove(blockState, level, blockPos, blockState2, bl);
            return;
        }

        FlightManager.INSTANCE.handleBlockBroken(level, blockPos, blockState);

//        BlockEntity blockEntity = level.getBlockEntity(blockPos);
//        if (blockEntity instanceof AscensionShardBlockEntity) {
//            for (UUID playerUUID : AscensionShardBlockEntity.weMadeFlying) {
//                Player player = level.getPlayerByUUID(playerUUID);
//                if (player != null) {
//                    String playerIflyTag = "ifly:" + blockPos.toShortString();
//                    if (player.getTags().contains(playerIflyTag)) {
//                        AscensionShardBlockEntity.weMadeFlying.remove(playerUUID);
//                        boolean wasFlying = player.getAbilities().flying;
//                        player.getAbilities().flying = false;
//                        player.getAbilities().mayfly = false;
//                        player.removeTag("ifly:" + blockPos.toShortString());
//                        double distanceToGround = getDistanceToGround(player);
//                        if (distanceToGround >= 4 && wasFlying) {
//                            int timeToFall = fallTimeCalc((int) Math.ceil(distanceToGround));
//                            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, timeToFall));
//                        }
//                        player.onUpdateAbilities();
//                    }
//                }
//            }
//        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if(level.isClientSide){
            NetworkManager.sendToServer(new C2SOpenIflyScreen(blockPos));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static class AscensionShardBlockItem extends BlockItem {
        public AscensionShardBlockItem(Block block) {
            super(block, new Properties());
        }

        @Override
        protected boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState) {
            if (blockPlaceContext.getLevel().isClientSide) {
                return super.placeBlock(blockPlaceContext, blockState);
            }

            var result = super.placeBlock(blockPlaceContext, blockState);
            if (result) {
                // Check if the block placed is an AscensionShardBlock
                var level = blockPlaceContext.getLevel();
                var blockAtPos = level.getBlockState(blockPlaceContext.getClickedPos());

                if (!blockAtPos.is(this.getBlock())) {
                    return true; // It's good but we just can't do anything with it
                }

                // Right cool, set the tile up for this player to be the owner
                FlightManager.INSTANCE.handleBlockPlaced(level, blockPlaceContext.getClickedPos(), blockState, blockPlaceContext.getPlayer());
            }

            return result;
        }
    }
}

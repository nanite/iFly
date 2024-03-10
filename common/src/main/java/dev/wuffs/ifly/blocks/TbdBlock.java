package dev.wuffs.ifly.blocks;

import dev.wuffs.ifly.network.C2SOpenIflyScreen;
import dev.wuffs.ifly.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TbdBlock extends Block implements EntityBlock {

    public TbdBlock() {
        super(Properties.of());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TbdBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : TbdBlockEntity::ticker;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if(level.isClientSide){
//            new TBDScreen(level.getBlockEntity(blockPos)).openGui();
            Network.CHANNEL.sendToServer(new C2SOpenIflyScreen(blockPos));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}

package dev.wuffs.ifly.blocks;

import dev.wuffs.ifly.network.C2SOpenIflyScreen;
import dev.wuffs.ifly.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static dev.wuffs.ifly.blocks.AscensionShardBlockEntity.fallTimeCalc;
import static dev.wuffs.ifly.blocks.AscensionShardBlockEntity.getDistanceToGround;

public class AscensionShardBlock extends Block implements EntityBlock {

    public AscensionShardBlock() {
        super(Properties.of().strength(0.4f, 3600000.0F).sound(SoundType.STONE).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AscensionShardBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : AscensionShardBlockEntity::ticker;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (level.isClientSide) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof AscensionShardBlockEntity ascensionShardBlockEntity) {
            ascensionShardBlockEntity.ownerUUID = livingEntity.getUUID();
            ascensionShardBlockEntity.setChanged();
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        if (blockEntity != null && ((AscensionShardBlockEntity) blockEntity).ownerUUID.equals(player.getUUID())) {
            super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (level.isClientSide) {
            super.onRemove(blockState, level, blockPos, blockState2, bl);
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof AscensionShardBlockEntity) {
            for (UUID playerUUID : AscensionShardBlockEntity.weMadeFlying) {
                Player player = level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    String playerIflyTag = "ifly:" + blockPos.toShortString();
                    if (player.getTags().contains(playerIflyTag)) {
                        AscensionShardBlockEntity.weMadeFlying.remove(playerUUID);
                        boolean wasFlying = player.getAbilities().flying;
                        player.getAbilities().flying = false;
                        player.getAbilities().mayfly = false;
                        player.removeTag("ifly:" + blockPos.toShortString());
                        double distanceToGround = getDistanceToGround(player);
                        if (distanceToGround >= 4 && wasFlying) {
                            int timeToFall = fallTimeCalc((int) Math.ceil(distanceToGround));
                            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, timeToFall));
                        }
                        player.onUpdateAbilities();
                    }
                }
            }
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if(level.isClientSide){
            Network.CHANNEL.sendToServer(new C2SOpenIflyScreen(blockPos));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
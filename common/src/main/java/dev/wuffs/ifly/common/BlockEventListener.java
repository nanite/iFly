package dev.wuffs.ifly.common;

import dev.architectury.event.EventResult;
import dev.architectury.utils.value.IntValue;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEventListener {

    public static EventResult onBlockBreakEvent(Level level, BlockPos pos, BlockState state, ServerPlayer player, IntValue xp){
        if (level == null || level.getServer() == null || level.isClientSide){
            return EventResult.pass();
        };

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AscensionShardBlockEntity asBlockEntity) {
            if (player.hasPermissions(Commands.LEVEL_GAMEMASTERS) || asBlockEntity.storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(player.getUUID()) && storedPlayer.level().isOwner())) {
                return EventResult.pass();
            }else {
                player.displayClientMessage(Component.literal("You are not the owner of this block!").withStyle(ChatFormatting.RED), true);
                return EventResult.interruptFalse();
            }
        }
        return EventResult.pass();
    }
}

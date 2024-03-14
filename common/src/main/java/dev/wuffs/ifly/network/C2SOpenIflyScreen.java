package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.network.records.AvailablePlayer;
import dev.wuffs.ifly.network.records.StoredPlayers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Supplier;

public class C2SOpenIflyScreen{

    BlockPos blockPos;
    public C2SOpenIflyScreen(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();

    }

    public C2SOpenIflyScreen(BlockPos pos) {
        // Message creation
        blockPos = pos;
    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeBlockPos(blockPos);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            BlockEntity blockEntity = contextSupplier.get().getPlayer().level().getBlockEntity(blockPos);
            if (blockEntity instanceof AscensionShardBlockEntity ascensionShardBlockEntity) {
                Player player = contextSupplier.get().getPlayer();
                if (ascensionShardBlockEntity.ownerUUID == null || !ascensionShardBlockEntity.ownerUUID.equals(player.getUUID())) {
                    player.displayClientMessage(Component.literal("You are not the owner of this block!").withStyle(ChatFormatting.RED), true);
                    return;
                }
                List<StoredPlayers> storedPlayers = ascensionShardBlockEntity.storedPlayers;
                List<AvailablePlayer> availablePlayers = contextSupplier.get().getPlayer().level().players().stream().map(player1 -> new AvailablePlayer(player1.getGameProfile())).toList();
                Network.CHANNEL.sendToPlayer((ServerPlayer) player, new S2COpenIflyScreen(blockPos, storedPlayers, availablePlayers, ascensionShardBlockEntity.ownerUUID));
            }
        });
    }
}

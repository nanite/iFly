package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.flight.FlightManager;
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
            var volume = FlightManager.INSTANCE.getVolume(blockPos);

            if (volume == null) {
                // What?! How did we get here?
                return;
            }

            Player player = contextSupplier.get().getPlayer();
            if (!volume.playerCanManage(player)) {
                player.displayClientMessage(Component.literal("You are not the owner/manager of this block!").withStyle(ChatFormatting.RED), true);
                return;
            }

            List<AvailablePlayer> availablePlayers = contextSupplier.get().getPlayer().level().players().stream().map(player1 -> new AvailablePlayer(player1.getGameProfile())).toList();
            List<FlightManager.FlightAccess> members = volume.members();

            Network.CHANNEL.sendToPlayer((ServerPlayer) player, new S2COpenIflyScreen(blockPos, members, availablePlayers));
        });
    }
}

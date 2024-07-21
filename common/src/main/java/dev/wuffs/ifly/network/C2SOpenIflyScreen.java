package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.flight.FlightManager;
import dev.wuffs.ifly.network.records.AvailablePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record C2SOpenIflyScreen(BlockPos blockPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SOpenIflyScreen> TYPE = new CustomPacketPayload.Type<>(AscensionShard.rl("open_screen_c2c"));

    public static final StreamCodec<FriendlyByteBuf, C2SOpenIflyScreen> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, C2SOpenIflyScreen::blockPos,
            C2SOpenIflyScreen::new
    );



    public static void handle(C2SOpenIflyScreen packet, NetworkManager.PacketContext contextSupplier) {
        // On receive
        contextSupplier.queue(() -> {
            Player player = contextSupplier.getPlayer();
            if (player == null) {
                return;
            }

            var volume = FlightManager.get((ServerLevel) player.level()).getVolume(packet.blockPos);

            if (!volume.playerCanManage(player)) {
                player.displayClientMessage(Component.literal("You are not the owner/manager of this block!").withStyle(ChatFormatting.RED), true);
                return;
            }

            List<AvailablePlayer> availablePlayers = contextSupplier.getPlayer().level().players().stream().map(player1 -> new AvailablePlayer(player1.getGameProfile())).toList();
            List<FlightManager.FlightAccess> members = volume.members();

//            NetworkManager.sendToPlayer((ServerPlayer) player, new S2COpenIflyScreen(packet.blockPos, availablePlayers, members));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

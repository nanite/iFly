package dev.wuffs.ifly.network;

import com.jcraft.jorbis.Block;
import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.client.gui.screen.AscensionShardScreen;
import dev.wuffs.ifly.common.PlayerLevel;
import dev.wuffs.ifly.flight.FlightManager;
import dev.wuffs.ifly.network.debug.C2SDebugScreen;
import dev.wuffs.ifly.network.records.AvailablePlayer;
import dev.wuffs.ifly.network.records.StoredPlayers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record S2COpenIflyScreen(BlockPos blockPos, List<AvailablePlayer> availablePlayers, List<StoredPlayers> members) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2COpenIflyScreen> TYPE = new CustomPacketPayload.Type<>(AscensionShard.rl("open_screen_2sc"));

    public static final StreamCodec<FriendlyByteBuf, S2COpenIflyScreen> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, S2COpenIflyScreen::blockPos,
            AvailablePlayer.STREAM_CODEC.apply(ByteBufCodecs.list()), S2COpenIflyScreen::availablePlayers,
            StoredPlayers.STREAM_CODEC.apply(ByteBufCodecs.list()), S2COpenIflyScreen::members,
            S2COpenIflyScreen::new
    );


    public static void handle(S2COpenIflyScreen screen, NetworkManager.PacketContext contextSupplier) {
        // On receive
        contextSupplier.queue(() -> {
            new AscensionShardScreen(screen.blockPos, screen.members, screen.availablePlayers).openGui();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

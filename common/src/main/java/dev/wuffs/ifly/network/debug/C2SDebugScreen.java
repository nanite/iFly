package dev.wuffs.ifly.network.debug;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.function.Supplier;

public record C2SDebugScreen() implements CustomPacketPayload {

    public static final Type<C2SDebugScreen> TYPE = new Type<>(AscensionShard.rl("debug_screen_c2s"));

    public static final StreamCodec<FriendlyByteBuf, C2SDebugScreen> STREAM_CODEC = StreamCodec.unit(new C2SDebugScreen());

    public static void handle(C2SDebugScreen packet, NetworkManager.PacketContext context) {
        // On receive
        context.queue(() -> {
            Player player = context.getPlayer();
            NetworkManager.sendToPlayer(((ServerPlayer) player), new S2CDebugScreen(AscensionShardBlockEntity.weMadeFlying, AscensionShardBlockEntity.alreadyFlying));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

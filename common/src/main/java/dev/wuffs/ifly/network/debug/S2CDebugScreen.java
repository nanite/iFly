package dev.wuffs.ifly.network.debug;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.client.gui.screen.DebugScreen;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record S2CDebugScreen(ObjectSet<UUID> weMadeFlying, ObjectSet<UUID> alreadyFlying) implements CustomPacketPayload {

    public static final Type<S2CDebugScreen> TYPE = new Type<>(AscensionShard.rl("debug_screen_s2c"));

    public static final StreamCodec<FriendlyByteBuf, S2CDebugScreen> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.collection(ObjectOpenHashSet::new)), S2CDebugScreen::weMadeFlying,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.collection(ObjectOpenHashSet::new)), S2CDebugScreen::alreadyFlying,
            S2CDebugScreen::new
    );


    public static void handle(S2CDebugScreen packet, NetworkManager.PacketContext contextSupplier) {
        // On receive
        contextSupplier.queue(() -> {
            new DebugScreen(packet.weMadeFlying, packet.alreadyFlying).openGui();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

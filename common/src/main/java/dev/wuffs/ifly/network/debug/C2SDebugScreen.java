package dev.wuffs.ifly.network.debug;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class C2SDebugScreen {
    public C2SDebugScreen(FriendlyByteBuf buf) {
    }

    public C2SDebugScreen() {
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            Player player = contextSupplier.get().getPlayer();
            Network.CHANNEL.sendToPlayer((ServerPlayer) player, new S2CDebugScreen());
        });
    }
}

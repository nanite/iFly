package dev.wuffs.ifly.network.debug;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.client.gui.screen.DebugScreen;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CDebugScreen {
    ObjectSet<UUID> weMadeFlying;
    ObjectSet<UUID> alreadyFlying;
    public S2CDebugScreen(FriendlyByteBuf buf) {
        ObjectSet<UUID> wf = new ObjectOpenHashSet<>();
        ObjectSet<UUID> af = new ObjectOpenHashSet<>();
        int wfSize = buf.readInt();
        int afSize = buf.readInt();

        for (int i = 0; i < wfSize; i++) {
            wf.add(buf.readUUID());
        }
        for (int i = 0; i < afSize; i++) {
            af.add(buf.readUUID());
        }
        weMadeFlying = wf;
        alreadyFlying = af;

    }

    public S2CDebugScreen() {
        this.weMadeFlying = AscensionShardBlockEntity.weMadeFlying;
        this.alreadyFlying = AscensionShardBlockEntity.alreadyFlying;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(weMadeFlying.size());
        buf.writeInt(alreadyFlying.size());
        for (UUID uuid : weMadeFlying) {
            // Encode data into the buf
            buf.writeUUID(uuid);
        }
        for (UUID uuid : alreadyFlying) {
            // Encode data into the buf
            buf.writeUUID(uuid);
        }
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            new DebugScreen(weMadeFlying, alreadyFlying).openGui();
        });
    }
}

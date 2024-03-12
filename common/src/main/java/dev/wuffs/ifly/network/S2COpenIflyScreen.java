package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.client.gui.screen.AscensionShardScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class S2COpenIflyScreen {

    BlockPos blockPos;
    List<AscensionShardBlockEntity.StoredPlayers> storedPlayers;
    UUID ownerUUID;
    public S2COpenIflyScreen(FriendlyByteBuf buf) {
        // Decode data into a message
        List<AscensionShardBlockEntity.StoredPlayers> sp = new ArrayList<>();
        int size = buf.readInt();
        blockPos = buf.readBlockPos();
        ownerUUID = buf.readUUID();
        for (int i = 0; i < size; i++) {
            sp.add(new AscensionShardBlockEntity.StoredPlayers(buf.readUUID(), buf.readComponent(), buf.readBoolean()));
        }
        storedPlayers = sp;

    }

    public S2COpenIflyScreen(BlockPos blockPos, List<AscensionShardBlockEntity.StoredPlayers> storedPlayers, UUID ownerUUID) {
        // Message creation
        this.blockPos = blockPos;
        this.storedPlayers = storedPlayers;
        this.ownerUUID = ownerUUID;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(storedPlayers.size());
        buf.writeBlockPos(blockPos);
        buf.writeUUID(ownerUUID);
        for (AscensionShardBlockEntity.StoredPlayers storedPlayer : storedPlayers) {
            // Encode data into the buf
            buf.writeUUID(storedPlayer.playerUUID());
            buf.writeComponent(storedPlayer.playerName());
            buf.writeBoolean(storedPlayer.allowed());
        }
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            // Handle message
            UUID playerUUID = contextSupplier.get().getPlayer().getUUID();

            new AscensionShardScreen(blockPos, storedPlayers, ownerUUID).openGui();
        });
    }
}

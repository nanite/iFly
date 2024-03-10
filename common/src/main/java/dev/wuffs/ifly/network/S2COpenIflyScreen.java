package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.TbdBlockEntity;
import dev.wuffs.ifly.client.gui.screen.TBDScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2COpenIflyScreen {

    BlockPos blockPos;
    List<TbdBlockEntity.StoredPlayers> storedPlayers;
    public S2COpenIflyScreen(FriendlyByteBuf buf) {
        // Decode data into a message
        List<TbdBlockEntity.StoredPlayers> sp = new ArrayList<>();
        int size = buf.readInt();
        blockPos = buf.readBlockPos();
        for (int i = 0; i < size; i++) {
            sp.add(new TbdBlockEntity.StoredPlayers(buf.readUUID(), buf.readBoolean()));
        }
        storedPlayers = sp;

    }

    public S2COpenIflyScreen(BlockPos blockPos, List<TbdBlockEntity.StoredPlayers> storedPlayers) {
        // Message creation
        this.blockPos = blockPos;
        this.storedPlayers = storedPlayers;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(storedPlayers.size());
        buf.writeBlockPos(blockPos);
        for (TbdBlockEntity.StoredPlayers storedPlayer : storedPlayers) {
            // Encode data into the buf
            buf.writeUUID(storedPlayer.playerUUID());
            buf.writeBoolean(storedPlayer.allowed());
        }
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            // Handle message
            new TBDScreen(blockPos, storedPlayers).openGui();
        });
    }
}

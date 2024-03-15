package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.api.PlayerLevel;
import dev.wuffs.ifly.client.gui.screen.AscensionShardScreen;
import dev.wuffs.ifly.network.records.AvailablePlayer;
import dev.wuffs.ifly.network.records.StoredPlayers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class S2COpenIflyScreen {

    BlockPos blockPos;
    List<StoredPlayers> storedPlayers;
    List<AvailablePlayer> availablePlayers;
//    List<> availableTeams;
    UUID ownerUUID;
    public S2COpenIflyScreen(FriendlyByteBuf buf) {
        // Decode data into a message
        List<StoredPlayers> sp = new ArrayList<>();
        List<AvailablePlayer> ap = new ArrayList<>();
        int spSize = buf.readInt();
        int apSize = buf.readInt();
        blockPos = buf.readBlockPos();

        for (int i = 0; i < apSize; i++) {
            ap.add(new AvailablePlayer(buf.readGameProfile()));
        }
        for (int i = 0; i < spSize; i++) {
            sp.add(new StoredPlayers(buf.readGameProfile(), buf.readEnum(PlayerLevel.class)));
        }
        availablePlayers = ap;
        storedPlayers = sp;

    }

    public S2COpenIflyScreen(BlockPos blockPos, List<StoredPlayers> storedPlayers, List<AvailablePlayer> availablePlayers) {
        // Message creation
        this.blockPos = blockPos;
        this.storedPlayers = storedPlayers;
        this.availablePlayers = availablePlayers;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(storedPlayers.size());
        buf.writeInt(availablePlayers.size());
        buf.writeBlockPos(blockPos);
        for (AvailablePlayer availablePlayer : availablePlayers) {
            // Encode data into the buf
            buf.writeGameProfile(availablePlayer.profile());
        }
        for (StoredPlayers storedPlayer : storedPlayers) {
            // Encode data into the buf
            buf.writeGameProfile(storedPlayer.player());
            buf.writeEnum(storedPlayer.level());
        }
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            new AscensionShardScreen(blockPos, storedPlayers, availablePlayers).openGui();
        });
    }
}

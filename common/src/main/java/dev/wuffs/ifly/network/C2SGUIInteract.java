package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.TbdBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class C2SGUIInteract {

    BlockPos blockPos;
    UUID playerUUID;
    boolean added;

    public C2SGUIInteract(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        playerUUID = buf.readUUID();
        added = buf.readBoolean();
    }

    public C2SGUIInteract(BlockPos pos, UUID playerUUID, boolean added) {
        // Message creation
        this.blockPos = pos;
        this.playerUUID = playerUUID;
        this.added = added;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeBlockPos(blockPos);
        buf.writeUUID(playerUUID);
        buf.writeBoolean(added);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            BlockEntity blockEntity = contextSupplier.get().getPlayer().level().getBlockEntity(blockPos);
            if (blockEntity instanceof TbdBlockEntity tbdBlockEntity) {
                if (!tbdBlockEntity.ownerUUID.equals(contextSupplier.get().getPlayer().getUUID())) {
                    return;
                }
                Player playerByUUID = contextSupplier.get().getPlayer().level().getPlayerByUUID(playerUUID);
                if (playerByUUID == null) {
                    return;
                }

                List<TbdBlockEntity.StoredPlayers> storedPlayers = tbdBlockEntity.storedPlayers;
                if (added) {
                    if (storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.playerUUID().equals(playerUUID))) {
                        return;
                    }
                    storedPlayers.add(new TbdBlockEntity.StoredPlayers(playerUUID, playerByUUID.getDisplayName(), true));
                } else {
                    storedPlayers.removeIf(storedPlayer -> storedPlayer.playerUUID().equals(playerUUID));
                }
                tbdBlockEntity.setChanged();
            }
        });
    }
}

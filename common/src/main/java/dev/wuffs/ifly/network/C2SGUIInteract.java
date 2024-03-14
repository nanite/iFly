package dev.wuffs.ifly.network;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.network.records.StoredPlayers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Supplier;

public class C2SGUIInteract {

    BlockPos blockPos;
    GameProfile profile;
    boolean added;

    public C2SGUIInteract(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        profile = buf.readGameProfile();
        added = buf.readBoolean();
    }

    public C2SGUIInteract(BlockPos pos, GameProfile profile, boolean added) {
        // Message creation
        this.blockPos = pos;
        this.profile = profile;
        this.added = added;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeBlockPos(blockPos);
        buf.writeGameProfile(profile);
        buf.writeBoolean(added);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            BlockEntity blockEntity = contextSupplier.get().getPlayer().level().getBlockEntity(blockPos);
            if (blockEntity instanceof AscensionShardBlockEntity ascensionShardBlockEntity) {
                if (!ascensionShardBlockEntity.ownerUUID.equals(contextSupplier.get().getPlayer().getUUID())) {
                    return;
                }
//                Player playerByUUID = contextSupplier.get().getPlayer().level().getPlayerByUUID(profile.getId());
//                if (playerByUUID == null) {
//                    return;
//                }

                List<StoredPlayers> storedPlayers = ascensionShardBlockEntity.storedPlayers;
                if (added) {
                    if (storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()))) {
                        return;
                    }
                    storedPlayers.add(new StoredPlayers(profile, true));
                } else {
                    storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                }
                ascensionShardBlockEntity.setChanged();
            }
        });
    }
}

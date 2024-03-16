package dev.wuffs.ifly.network;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.common.PlayerLevel;
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
    PlayerLevel level;

    public C2SGUIInteract(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        profile = buf.readGameProfile();
        level = buf.readEnum(PlayerLevel.class);
    }

    public C2SGUIInteract(BlockPos pos, GameProfile profile, PlayerLevel level) {
        // Message creation
        this.blockPos = pos;
        this.profile = profile;
        this.level = level;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeBlockPos(blockPos);
        buf.writeGameProfile(profile);
        buf.writeEnum(level);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            BlockEntity blockEntity = contextSupplier.get().getPlayer().level().getBlockEntity(blockPos);
            if (blockEntity instanceof AscensionShardBlockEntity ascensionShardBlockEntity) {
                List<StoredPlayers> storedPlayers = ascensionShardBlockEntity.storedPlayers;
                boolean isPlayerManagerOrGreater = storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(contextSupplier.get().getPlayer().getUUID()) && storedPlayer.level().isManagerOrGreater());
                if (!isPlayerManagerOrGreater) {
                    return;
                }

                switch (level) {
                    case REMOVE:
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                        break;
                    case MEMBER:
                        if (storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()))) {
                            storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                        }
                        storedPlayers.add(new StoredPlayers(profile, PlayerLevel.MEMBER));
                        break;
                    case MANAGER:
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                        storedPlayers.add(new StoredPlayers(profile, PlayerLevel.MANAGER));
                        break;
                    case OWNER:
                        // Remove the user from the list if they are already in it, so we can re-add them as the owner
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                        // Remove the current owner from the list and add them back as a normal user
                        GameProfile currentOwner = storedPlayers.stream().filter(storedPlayer -> storedPlayer.level().isOwner()).findFirst().get().player();
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(currentOwner.getId()));
                        storedPlayers.add(new StoredPlayers(currentOwner, PlayerLevel.MEMBER));

                        // Add the new owner
                        storedPlayers.add(new StoredPlayers(profile, PlayerLevel.OWNER));
                        break;
                }
                ascensionShardBlockEntity.setChanged();
            }
        });
    }
}

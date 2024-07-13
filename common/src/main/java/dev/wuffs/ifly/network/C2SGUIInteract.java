package dev.wuffs.ifly.network;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.common.PlayerLevel;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.network.debug.C2SDebugScreen;
import dev.wuffs.ifly.network.records.StoredPlayers;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record C2SGUIInteract(BlockPos blockPos, GameProfile profile, PlayerLevel level) implements CustomPacketPayload{

    public static final CustomPacketPayload.Type<C2SGUIInteract> TYPE = new CustomPacketPayload.Type<>(AscensionShard.rl("gui_interact"));

    public static final StreamCodec<ByteBuf, C2SGUIInteract> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, C2SGUIInteract::blockPos,
            ByteBufCodecs.GAME_PROFILE, C2SGUIInteract::profile,
            PlayerLevel.STREAM_CODEC, C2SGUIInteract::level,
            C2SGUIInteract::new
    );


    public static void handle(C2SGUIInteract packet, NetworkManager.PacketContext contextSupplier) {
        // On receive
        contextSupplier.queue(() -> {
            BlockEntity blockEntity = contextSupplier.getPlayer().level().getBlockEntity(packet.blockPos);
            if (blockEntity instanceof AscensionShardBlockEntity ascensionShardBlockEntity) {
                List<StoredPlayers> storedPlayers = ascensionShardBlockEntity.storedPlayers;
                boolean isPlayerManagerOrGreater = storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(contextSupplier.getPlayer().getUUID()) && storedPlayer.level().isManagerOrGreater());
                if (!isPlayerManagerOrGreater) {
                    return;
                }

                switch (packet.level) {
                    case REMOVE:
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(packet.profile.getId()));
                        break;
                    case MEMBER:
                        if (storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(packet.profile.getId()))) {
                            storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(packet.profile.getId()));
                        }
                        storedPlayers.add(new StoredPlayers(packet.profile, PlayerLevel.MEMBER));
                        break;
                    case MANAGER:
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(packet.profile.getId()));
                        storedPlayers.add(new StoredPlayers(packet.profile, PlayerLevel.MANAGER));
                        break;
                    case OWNER:
                        // Remove the user from the list if they are already in it, so we can re-add them as the owner
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(packet.profile.getId()));
                        // Remove the current owner from the list and add them back as a normal user
                        GameProfile currentOwner = storedPlayers.stream().filter(storedPlayer -> storedPlayer.level().isOwner()).findFirst().get().player();
                        storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(currentOwner.getId()));
                        storedPlayers.add(new StoredPlayers(currentOwner, PlayerLevel.MEMBER));

                        // Add the new owner
                        storedPlayers.add(new StoredPlayers(packet.profile, PlayerLevel.OWNER));
                        break;
                }
                ascensionShardBlockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

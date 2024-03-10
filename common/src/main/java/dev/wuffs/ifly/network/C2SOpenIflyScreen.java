package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkManager;
import dev.wuffs.ifly.blocks.TbdBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Supplier;

public class C2SOpenIflyScreen{

    BlockPos blockPos;
    public C2SOpenIflyScreen(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();

    }

    public C2SOpenIflyScreen(BlockPos pos) {
        // Message creation
        blockPos = pos;
    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeBlockPos(blockPos);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
        contextSupplier.get().queue(() -> {
            BlockEntity blockEntity = contextSupplier.get().getPlayer().level().getBlockEntity(blockPos);
            if (blockEntity instanceof TbdBlockEntity tbdBlockEntity) {
                List<TbdBlockEntity.StoredPlayers> storedPlayers = tbdBlockEntity.storedPlayers;
                Network.CHANNEL.sendToPlayer((ServerPlayer) contextSupplier.get().getPlayer(), new S2COpenIflyScreen(blockPos, storedPlayers));
            }
        });
    }
}

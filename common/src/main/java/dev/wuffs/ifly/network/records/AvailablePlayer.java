package dev.wuffs.ifly.network.records;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AvailablePlayer(GameProfile profile) {

    public static final StreamCodec<ByteBuf, AvailablePlayer> STREAM_CODEC = ByteBufCodecs.GAME_PROFILE
            .map(AvailablePlayer::new, AvailablePlayer::profile);


}

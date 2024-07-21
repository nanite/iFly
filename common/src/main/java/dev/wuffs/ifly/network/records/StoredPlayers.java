package dev.wuffs.ifly.network.records;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.wuffs.ifly.common.PlayerLevel;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

/**
 * @deprecated {@link dev.wuffs.ifly.flight.FlightManager}
 */
public record StoredPlayers(
        GameProfile player,
        PlayerLevel level
) {
    public static final Codec<StoredPlayers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.GAME_PROFILE.fieldOf("player").forGetter(StoredPlayers::player),
            PlayerLevel.CODEC.fieldOf("level").forGetter(StoredPlayers::level)
    ).apply(instance, StoredPlayers::new));

    public static final StreamCodec<ByteBuf, StoredPlayers> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.GAME_PROFILE, StoredPlayers::player,
            PlayerLevel.STREAM_CODEC, StoredPlayers::level,
            StoredPlayers::new
    );

    public static final Codec<List<StoredPlayers>> LIST_CODEC = Codec.list(CODEC);
}


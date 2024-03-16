package dev.wuffs.ifly.network.records;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.wuffs.ifly.common.PlayerLevel;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record StoredPlayers(
        GameProfile player,
        PlayerLevel level
) {
    public static final Codec<StoredPlayers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.GAME_PROFILE.fieldOf("player").forGetter(StoredPlayers::player),
            PlayerLevel.CODEC.fieldOf("level").forGetter(StoredPlayers::level)
    ).apply(instance, StoredPlayers::new));

    public static final Codec<List<StoredPlayers>> LIST_CODEC = Codec.list(CODEC);
}


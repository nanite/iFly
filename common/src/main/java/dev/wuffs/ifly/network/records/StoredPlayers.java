package dev.wuffs.ifly.network.records;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record StoredPlayers(
        GameProfile player,
        boolean allowed
) {
    public static final Codec<StoredPlayers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            UUIDUtil.CODEC.fieldOf("teamUUID").forGetter(StoredPlayers::playerUUID),
//            ComponentSerialization.CODEC.fieldOf("teamName").forGetter(StoredPlayers::playerName),
            ExtraCodecs.GAME_PROFILE.fieldOf("player").forGetter(StoredPlayers::player),
            Codec.BOOL.fieldOf("allowed").forGetter(StoredPlayers::allowed)
    ).apply(instance, StoredPlayers::new));

    public static final Codec<List<StoredPlayers>> LIST_CODEC = Codec.list(CODEC);
}

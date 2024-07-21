package dev.wuffs.ifly.network.records;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record AvailableTeam(UUID teamUUID, Component teamName, int memberCount) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AvailableTeam> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AvailableTeam::teamUUID,
            ComponentSerialization.STREAM_CODEC, AvailableTeam::teamName,
            ByteBufCodecs.INT, AvailableTeam::memberCount,
            AvailableTeam::new
    );
}

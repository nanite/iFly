package dev.wuffs.ifly.common;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.wuffs.ifly.flight.FlightManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.Cod;

import java.util.Optional;

/**
 * @deprecated Use {@link FlightManager.Role} instead
 */
public enum PlayerLevel implements StringRepresentable {
    OWNER("owner", 100, Icons.DIAMOND),
    MANAGER("manager", 50, Icons.SHIELD),
    MEMBER("member", 10),
    REMOVE("remove", 0);

    public static final Codec<PlayerLevel> CODEC = StringRepresentable.fromEnum(PlayerLevel::values);

    //TODO FIX THIS
    public static final StreamCodec<ByteBuf, PlayerLevel> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    private final String name;
    private final int power;
    private final Icon icon;

    PlayerLevel(String name, int power, Icon icon) {
        this.name = name;
        this.power = power;
        this.icon = icon;
    }

    PlayerLevel(String name, int power) {
        this(name, power, null);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public Optional<Icon> getIcon() {
        return Optional.ofNullable(icon);
    }

    public Component getDisplayName() {
        return Component.literal(name);
    }

    public boolean isAtLeast(PlayerLevel level) {
        return level.power >= 0 ?
                power >= level.power :
                power <= level.power;
    }

    public boolean isMemberOrGreater() {
        return isAtLeast(MEMBER);
    }

    public boolean isManagerOrGreater() {
        return isAtLeast(MANAGER);
    }

    public boolean isOwner() {
        return isAtLeast(OWNER);
    }
}

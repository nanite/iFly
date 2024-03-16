package dev.wuffs.ifly.common;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;

public enum PlayerLevel implements StringRepresentable {
    OWNER("owner", 100, Icons.DIAMOND),
    MANAGER("manager", 50, Icons.SHIELD),
    MEMBER("member", 10),
    REMOVE("remove", 0);

    public static final Codec<PlayerLevel> CODEC = StringRepresentable.fromEnum(PlayerLevel::values);

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

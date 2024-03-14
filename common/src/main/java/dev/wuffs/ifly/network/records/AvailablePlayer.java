package dev.wuffs.ifly.network.records;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;

public record AvailablePlayer(GameProfile profile) {
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeGameProfile(this.profile());
    }

    public static AvailablePlayer readFromNetwork(FriendlyByteBuf buf) {
        return new AvailablePlayer(buf.readGameProfile());
    }
}

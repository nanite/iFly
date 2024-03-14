package dev.wuffs.ifly.network.records;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public record AvailableTeam(UUID teamUUID, Component teamName, int memberCount) {
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeUUID(this.teamUUID());
        buf.writeComponent(this.teamName());
        buf.writeInt(this.memberCount());
    }

    public static AvailableTeam readFromNetwork(FriendlyByteBuf buf) {
        return new AvailableTeam(buf.readUUID(), buf.readComponent(), buf.readInt());
    }
}

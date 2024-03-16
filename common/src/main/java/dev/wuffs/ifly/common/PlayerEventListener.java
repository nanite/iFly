package dev.wuffs.ifly.common;

import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEventListener {
    public static void onPlayerQuitEvent(ServerPlayer player) {
        if (player != null) {
            boolean wfContainsPlayer = AscensionShardBlockEntity.weMadeFlying.contains(player.getUUID());
            if (wfContainsPlayer) {
                AscensionShardBlockEntity.weMadeFlying.remove(player.getUUID());
            }
            boolean afContainsPlayer = AscensionShardBlockEntity.alreadyFlying.contains(player.getUUID());
            if (afContainsPlayer) {
                AscensionShardBlockEntity.alreadyFlying.remove(player.getUUID());
            }
        }
    }
}

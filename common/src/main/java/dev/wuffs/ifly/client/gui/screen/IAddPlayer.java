package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;

public interface IAddPlayer {
    boolean isPlayerAdded(GameProfile profile);
    void setPlayerAdded(GameProfile profile, boolean added);
}

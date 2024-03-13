package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;

public interface AddPlayerSetup {
    boolean isAdded(GameProfile profile);
    void setAdded(GameProfile profile, boolean added);
}

package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;

public interface IAddTeam {
    boolean isTeamAdded(GameProfile profile);
    void setTeamAdded(GameProfile profile, boolean added);
}

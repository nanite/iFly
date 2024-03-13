package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.icon.FaceIcon;
import dev.ftb.mods.ftblibrary.ui.NordButton;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PlayerButton extends NordButton {
    public final GameProfile player;
    public final AddPlayerSetup screen;

    public PlayerButton(Panel panel, AddPlayerSetup setup, GameProfile player) {
        super(panel, checkbox(false).append(" " + player.getName()), FaceIcon.getFace(player));
        this.player = player;
        this.screen = setup;
    }

    private static MutableComponent checkbox(boolean checked) {
        return checked ? Component.literal("☑").withStyle(ChatFormatting.GREEN) : Component.literal("☐");
    }

    @Override
    public void onClicked(MouseButton button) {
        boolean invited = screen.isAdded(player);
        screen.setAdded(player, !invited);
        title = checkbox(!invited).append(" " + player.getName());
    }
}

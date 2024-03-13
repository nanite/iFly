package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.network.C2SGUIInteract;
import dev.wuffs.ifly.network.Network;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AddPlayerScreen extends BaseScreen implements NordColors, AddPlayerSetup {

    protected final Set<GameProfile> invites = new HashSet<>();

    private Panel playerPanel;
    private Button executeButton;
    private Button closeButton;
    private final Component title;

    private final List<AscensionShardBlockEntity.StoredPlayers> storedPlayers;
    private final BlockPos blockPos;
    private final UUID ownerUUID;

    public AddPlayerScreen(Component title, List<AscensionShardBlockEntity.StoredPlayers> storedPlayers, BlockPos blockPos, UUID ownerUUID) {
        this.title = title;
        this.storedPlayers = storedPlayers;
        this.blockPos = blockPos;
        this.ownerUUID = ownerUUID;
    }

    @Override
    public boolean onInit() {
        setWidth(150);
        setHeight(getScreen().getGuiScaledHeight() * 3 / 4);
        return true;
    }

    @Override
    public void addWidgets() {
        add(closeButton = new SimpleButton(this, Component.literal("Close"), Icons.CANCEL.withTint(SNOW_STORM_2), (simpleButton, mouseButton) -> closeGui()) {
            @Override
            public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                drawIcon(graphics, theme, x, y, w, h);
            }
        });
        add(playerPanel = new PlayerButtonPanel());
        add(executeButton = new ExecuteButton(Component.literal("Add"), Icons.ADD, () -> {
            for (GameProfile invite : invites) {
                Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, invite.getId(), true));
                storedPlayers.add(new AscensionShardBlockEntity.StoredPlayers(invite.getId(), Component.literal(invite.getName()), true));
            }
            closeGui();
        }));
    }

    @Override
    public void alignWidgets() {
        closeButton.setPosAndSize(width - 20, 2, 16, 16);
        playerPanel.setPosAndSize(2, 20, width - 4, height - 40);
        executeButton.setPosAndSize(this.width / 2 - 40, height - 18, 80, 16);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        POLAR_NIGHT_1.draw(graphics, x + playerPanel.posX, y + playerPanel.posY, playerPanel.width, playerPanel.height);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + h - 20, w - 2, 18);
    }

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawString(graphics, title, x + w / 2, y + 5, SNOW_STORM_1, Theme.CENTERED);
    }

    @Override
    public boolean isAdded(GameProfile profile) {
        return invites.contains(profile);
    }

    @Override
    public void setAdded(GameProfile profile, boolean added) {
        if (added) {
            invites.add(profile);
        } else {
            invites.remove(profile);
        }
    }

    private class PlayerButtonPanel extends Panel {
        public PlayerButtonPanel() {
            super(AddPlayerScreen.this);
        }

        @Override
        public void addWidgets() {
            var playerList = Minecraft.getInstance().level.players();
            if (playerList.isEmpty()) {
                add(new TextField(this).setText(Component.literal("No players available").withStyle(ChatFormatting.ITALIC)).addFlags(Theme.CENTERED));
            } else {
                playerList.forEach(player -> add(new PlayerButton(this, AddPlayerScreen.this, player.getGameProfile())));
            }
        }

        @Override
        public void alignWidgets() {
            align(new WidgetLayout.Vertical(2, 1, 2));
            widgets.forEach(w -> w.setX(4));
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            NordColors.POLAR_NIGHT_2.draw(graphics, x, y, w, h / 2);
        }
    }

    protected class ExecuteButton extends NordButton {
        private final Component titleDark;
        private final Runnable callback;

        public ExecuteButton(Component txt, Icon icon, Runnable callback) {
            super(AddPlayerScreen.this, txt, icon);
            this.titleDark = title.copy().withStyle(Style.EMPTY.withColor(POLAR_NIGHT_0.rgb()));
            this.callback = callback;
        }

        @Override
        public void onClicked(MouseButton button) {
            if (isEnabled()) callback.run();
        }

        @Override
        public Component getTitle() {
            return isEnabled() ? title : titleDark;
        }

        @Override
        public boolean renderTitleInCenter() {
            return true;
        }
    }
}

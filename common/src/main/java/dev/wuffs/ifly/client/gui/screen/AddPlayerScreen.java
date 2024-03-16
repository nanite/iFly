package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import dev.wuffs.ifly.common.PlayerLevel;
import dev.wuffs.ifly.network.C2SGUIInteract;
import dev.wuffs.ifly.network.Network;
import dev.wuffs.ifly.network.records.AvailablePlayer;
import dev.wuffs.ifly.network.records.StoredPlayers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddPlayerScreen extends BaseScreen implements NordColors, IAddPlayer, IAddTeam {

    protected final Set<GameProfile> playerInvites = new HashSet<>();
    protected final Set<GameProfile> teamInvites = new HashSet<>();

    private Panel playerPanel;
    private Panel teamPanel;
    private Button executeButton;
    private Button closeButton;

    private final List<StoredPlayers> storedPlayers;
    private final List<AvailablePlayer> availablePlayers;
    private final BlockPos blockPos;

    public AddPlayerScreen(List<StoredPlayers> storedPlayers, List<AvailablePlayer> availablePlayers, BlockPos blockPos) {
        this.storedPlayers = storedPlayers;
        this.availablePlayers = availablePlayers.stream()
//                .filter((availablePlayer) -> !availablePlayer.profile().getId().equals(ownerUUID))
                .filter((availablePlayer) -> storedPlayers.stream().noneMatch(storedPlayer -> storedPlayer.player().getId().equals(availablePlayer.profile().getId())))
                .toList();
        this.blockPos = blockPos;
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
        add(teamPanel = new TeamButtonPanel());
        add(executeButton = new ExecuteButton(Component.literal("Add"), Icons.ADD, () -> {
            // Player Stuff
            for (GameProfile invite : playerInvites) {
                Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, invite, PlayerLevel.MEMBER));
                storedPlayers.add(new StoredPlayers(invite, PlayerLevel.MEMBER));
            }
            // Team Stuff
            for (GameProfile invite : teamInvites) {
                System.out.println("Team invite: " + invite.getName());
            }
            closeGui();
        }));
    }

    @Override
    public void alignWidgets() {
        closeButton.setPosAndSize(width - 20, 2, 16, 16);
        playerPanel.setPosAndSize(2, 20, width - 4, height / 2 - 30);
        teamPanel.setPosAndSize(2, (height / 2) + 10, width - 4, height / 2 - 30);
        executeButton.setPosAndSize(this.width / 2 - 40, height - 18, 80, 16);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 1, w - 2, h - 2);
    }

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawString(graphics, Component.literal("Add Player(s)"), x + w / 2, y + 6, SNOW_STORM_1, Theme.CENTERED);
        theme.drawString(graphics, Component.literal("Add Team(s)"), x + w / 2, y + (h / 2) - 3, SNOW_STORM_1, Theme.CENTERED);
    }

    @Override
    public boolean isPlayerAdded(GameProfile profile) {
        return playerInvites.contains(profile);
    }

    @Override
    public void setPlayerAdded(GameProfile profile, boolean added) {
        if (added) {
            playerInvites.add(profile);
        } else {
            playerInvites.remove(profile);
        }
    }

    @Override
    public boolean isTeamAdded(GameProfile profile) {
        return teamInvites.contains(profile);
    }

    @Override
    public void setTeamAdded(GameProfile profile, boolean added) {
        if (added) {
            teamInvites.add(profile);
        } else {
            teamInvites.remove(profile);
        }
    }

    private class PlayerButtonPanel extends Panel {
        public PlayerButtonPanel() {
            super(AddPlayerScreen.this);
        }

        @Override
        public void addWidgets() {
            if (availablePlayers.isEmpty()) {
                add(new TextField(this).setText(Component.literal("No selectable players").withStyle(ChatFormatting.ITALIC)).addFlags(Theme.CENTERED));
            } else {
                availablePlayers.forEach(player -> {
//                    if (!player.profile().getId().equals(ownerUUID) && !storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(player.profile().getId()))) {
                        add(new PlayerButton(this, AddPlayerScreen.this, player.profile()));
//                    }
                });
            }
        }

        @Override
        public void alignWidgets() {
            align(new WidgetLayout.Vertical(2, 1, 2));
            widgets.forEach(w -> w.setX(4));
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            NordColors.POLAR_NIGHT_2.draw(graphics, x, y, w, h);
        }
    }

    private class TeamButtonPanel extends Panel {
        public TeamButtonPanel() {
            super(AddPlayerScreen.this);
        }

        @Override
        public void addWidgets() {
                add(new TextField(this).setText(Component.literal("Not implemented yet").withStyle(ChatFormatting.ITALIC)).addFlags(Theme.CENTERED));
        }

        @Override
        public void alignWidgets() {
            align(new WidgetLayout.Vertical(2, 1, 2));
            widgets.forEach(w -> w.setX(4));
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            NordColors.POLAR_NIGHT_2.draw(graphics, x, y, w, h);
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

package dev.wuffs.ifly.client.gui.screen;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.network.C2SGUIInteract;
import dev.wuffs.ifly.network.Network;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class AscensionShardScreen extends BaseScreen implements NordColors {

    private BlankPanel whitelistedPlayersPanel;
    private PanelScrollBar whitelistedPlayersScroll;

    private BlankPanel onlinePlayersPanel;
    private PanelScrollBar onlinePlayersScroll;

    private Button infoButton;

    public BlockPos blockPos;
    public UUID ownerUUID;
    public List<AscensionShardBlockEntity.StoredPlayers> storedPlayers;

    private static final int scrollWidth = 10;

    public AscensionShardScreen(BlockPos blockPos, List<AscensionShardBlockEntity.StoredPlayers> storedPlayers, UUID ownerUUID) {
        super();
        this.blockPos = blockPos;
        this.storedPlayers = storedPlayers;
        this.ownerUUID = ownerUUID;
    }

    @Override
    public boolean onInit() {
        setSizeProportional(0.70f, 0.7f);

        return super.onInit();
    }

    @Override
    public void alignWidgets() {
//        align(WidgetLayout.VERTICAL);
        whitelistedPlayersPanel.alignWidgets();
        onlinePlayersPanel.alignWidgets();
        infoButton.setPosAndSize(5, 3, 16, 16);
    }

    @Override
    public void addWidgets() {

        add(infoButton = new SimpleButton(this, Component.empty(), Icons.INFO, (w, mb) -> {
        }) {
            @Override
            public void addMouseOverText(TooltipList list) {
                addIflyInfo(list);
            }

            @Override
            public void playClickSound() {
            }
        });

        whitelistedPlayersPanel = new BlankPanel(this) {
            @Override
            public void alignWidgets() {
                align(WidgetLayout.VERTICAL);
            }
        };
        whitelistedPlayersScroll = new PanelScrollBar(this, whitelistedPlayersPanel);

        onlinePlayersPanel = new BlankPanel(this) {
            @Override
            public void alignWidgets() {
                align(WidgetLayout.VERTICAL);
            }
        };
        onlinePlayersScroll = new PanelScrollBar(this, onlinePlayersPanel);

        Player ownerByUUID = Minecraft.getInstance().level.getPlayerByUUID(this.ownerUUID);
        var ownerEntry = getEntry(whitelistedPlayersPanel, ownerByUUID.getDisplayName().getString(), Icons.STAR, this.ownerUUID, false, false);
        whitelistedPlayersPanel.add(ownerEntry);

        for (AscensionShardBlockEntity.StoredPlayers player : storedPlayers) {
            var playerEntry = getEntry(whitelistedPlayersPanel, player.playerName().getString(), Icons.REMOVE, player.playerUUID(), false, true);
            whitelistedPlayersPanel.add(playerEntry);
        }

        whitelistedPlayersPanel.setPosAndSize(5, 43, width / 2, height - 44);
        whitelistedPlayersScroll.setPosAndSize(width / 2 - scrollWidth, 40, scrollWidth, height - 41);

        this.add(whitelistedPlayersPanel);
        this.add(whitelistedPlayersScroll);

        var playerList = Minecraft.getInstance().level.players();
        for (AbstractClientPlayer player : playerList) {
            if (player.getUUID().equals(ownerUUID)) {
                continue;
            }
            if (storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.playerUUID().equals(player.getUUID()))) {
                continue;
            }
            var playerEntry = getEntry(onlinePlayersPanel, player.getDisplayName().getString(), Icons.ADD, player.getUUID(), true, true);
            playerEntry.setWidth(this.width - 5);
            onlinePlayersPanel.add(playerEntry);
        }

        onlinePlayersPanel.setPosAndSize(width / 2 + 5, 43, width / 2, height - 44);
        onlinePlayersScroll.setPosAndSize(width - scrollWidth, 40, scrollWidth, height - 41);
        this.add(onlinePlayersPanel);
        this.add(onlinePlayersScroll);
    }

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        super.drawForeground(graphics, theme, x, y, w, h);
        theme.drawString(graphics, "Player Manager", x + w / 2, y + 7, SNOW_STORM_1, Theme.CENTERED);
        theme.drawString(graphics, "Whitelisted Players", x + w / 4, y + 27, SNOW_STORM_1, Theme.CENTERED);
        theme.drawString(graphics, "Online Players", (x + w / 2) + Minecraft.getInstance().font.width("Online Players"), y + 27, SNOW_STORM_1, 0);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_1.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 21, w - 2, 1);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 41, w - 2, 1);
        POLAR_NIGHT_0.draw(graphics, x + w / 2, y + 22, 1, h - 22);
    }

    private BlankPanel getEntry(Panel panel, String btnText, Icon icon, UUID playerUUID, boolean isAdding, boolean isEnabled) {
        var playerEntry = new BlankPanel(panel) {
            @Override
            public void alignWidgets() {
                align(WidgetLayout.HORIZONTAL);
            }

            @Override
            public void addWidgets() {
                SimpleTextButton playerBtn = new SimpleTextButton(this, Component.literal(btnText), icon) {
                    @Override
                    public void onClicked(MouseButton button) {
                        if (!isEnabled) {
                            return;
                        }
                        Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, playerUUID, isAdding));
                        if (isAdding) {
                            storedPlayers.add(new AscensionShardBlockEntity.StoredPlayers(playerUUID, Component.literal(btnText), isAdding));
                        } else {
                            storedPlayers.removeIf(storedPlayer -> storedPlayer.playerUUID().equals(playerUUID));
                        }
                        AscensionShardScreen.this.refreshWidgets();
                    }

                    @Override
                    public boolean isEnabled() {
                        return isEnabled;
                    }
                };
                playerBtn.setWidth(panel.width - scrollWidth - 7);
                this.add(playerBtn);
                this.setHeight(playerBtn.height + 3);
            }
        };
        playerEntry.setWidth(this.width - 5);
        return playerEntry;
    }

    private void addIflyInfo(TooltipList list) {

        Player playerByUUID = Minecraft.getInstance().level.getPlayerByUUID(this.ownerUUID);
        list.add(Component.literal("Owner: " + playerByUUID.getDisplayName().getString()).withStyle(ChatFormatting.AQUA));
        list.add(Component.literal("UUID: " + this.ownerUUID).withStyle(ChatFormatting.YELLOW));
    }
}
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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class AscensionShardScreen extends BaseScreen implements NordColors {

    private BlankPanel whitelistedPlayersPanel;

    private Button infoButton;
    private Button addButton;

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
        setWidth(150);
        setHeight(getScreen().getGuiScaledHeight() * 3 / 4);
        return super.onInit();
    }

    @Override
    public void alignWidgets() {
        infoButton.setPosAndSize(5, 3, 16, 16);
        addButton.setPosAndSize(width - 20, 2, 16, 16);
        whitelistedPlayersPanel.alignWidgets();
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

        add(addButton = new SimpleButton(this, Component.literal("Add"), Icons.ADD.withTint(SNOW_STORM_2), (simpleButton, mouseButton) -> closeGui()) {
            @Override
            public void onClicked(MouseButton button) {
                new AddPlayerScreen(Component.literal("Add Player(s)"), storedPlayers, blockPos, ownerUUID).openGui();
            }
            @Override
            public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                drawIcon(graphics, theme, x, y, w, h);
            }
        });

        whitelistedPlayersPanel = new BlankPanel(this) {
            @Override
            public void alignWidgets() {
                align(WidgetLayout.VERTICAL);
            }
        };

        Player ownerByUUID = Minecraft.getInstance().level.getPlayerByUUID(this.ownerUUID);
        var ownerEntry = getEntry(whitelistedPlayersPanel, ownerByUUID.getDisplayName().getString(), Icons.STAR, this.ownerUUID, false, false);
        whitelistedPlayersPanel.add(ownerEntry);

        for (int i = 0; i < 30; i++) {
            for (AscensionShardBlockEntity.StoredPlayers player : storedPlayers) {
                var playerEntry = getEntry(whitelistedPlayersPanel, player.playerName().getString(), Icons.REMOVE, player.playerUUID(), false, true);
                whitelistedPlayersPanel.add(playerEntry);
            }
        }


        whitelistedPlayersPanel.setPosAndSize(5, 23, width - 10, height - 28);

        this.add(whitelistedPlayersPanel);
    }

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        super.drawForeground(graphics, theme, x, y, w, h);
        theme.drawString(graphics, "Player Manager", x + w / 2, y + 7, SNOW_STORM_1, Theme.CENTERED);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_1.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        POLAR_NIGHT_0.draw(graphics, x + whitelistedPlayersPanel.posX, y + whitelistedPlayersPanel.posY, whitelistedPlayersPanel.width, whitelistedPlayersPanel.height);
    }

    private BlankPanel getEntry(Panel panel, String btnText, Icon icon, UUID playerUUID, boolean isAdding, boolean isEnabled) {
        var playerEntry = new BlankPanel(panel) {

            @Override
            public void addWidgets() {
                SimpleTextButton playerBtn = new NordButton(this, Component.literal(btnText), icon) {
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
                playerBtn.setWidth(panel.width - 8);
                this.add(playerBtn);
                this.setHeight(playerBtn.height + 3);
            }

            @Override
            public void alignWidgets() {
                align(new WidgetLayout.Vertical(2, 0, 2));
                widgets.forEach(w -> w.setX(4));
            }
        };
        playerEntry.setWidth(this.width);
        return playerEntry;
    }

    private void addIflyInfo(TooltipList list) {

        Player playerByUUID = Minecraft.getInstance().level.getPlayerByUUID(this.ownerUUID);
        list.add(Component.literal("Owner: " + playerByUUID.getDisplayName().getString()).withStyle(ChatFormatting.AQUA));
        list.add(Component.literal("UUID: " + this.ownerUUID).withStyle(ChatFormatting.YELLOW));
    }
}

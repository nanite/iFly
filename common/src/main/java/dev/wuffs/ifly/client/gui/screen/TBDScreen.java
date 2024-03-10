package dev.wuffs.ifly.client.gui.screen;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import dev.wuffs.ifly.blocks.TbdBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TBDScreen extends BaseScreen implements NordColors {

    private BlankPanel whitelistedPlayersPanel;
    private PanelScrollBar whitelistedPlayersScroll;

    private BlankPanel onlinePlayersPanel;
    private PanelScrollBar onlinePlayersScroll;

    public BlockPos blockPos;
    public List<TbdBlockEntity.StoredPlayers> storedPlayers;

    private static final int scrollWidth = 12;

    public TBDScreen(BlockPos blockPos, List<TbdBlockEntity.StoredPlayers> storedPlayers) {
        super();
        this.blockPos = blockPos;
        this.storedPlayers = storedPlayers;
    }

    @Override
    public boolean onInit() {
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

        setSizeProportional(0.70f, 0.7f);

        return super.onInit();
    }

    @Override
    public void alignWidgets() {
//        align(WidgetLayout.VERTICAL);
        whitelistedPlayersPanel.alignWidgets();
        onlinePlayersPanel.alignWidgets();
    }

    @Override
    public void addWidgets() {
        this.add(new SimpleButton(this, Component.empty(), Icons.ADD, (btn, mb) -> {
            new PlayerListScreen(this, blockPos).openGui();
        }));
        var things = List.of("Fuck you mikey w");
        for (int i = 0; i < 10; i++) {
            for (String thing : things) {
                var playerEntry = getEntry(whitelistedPlayersPanel,thing, Icons.REMOVE);
                whitelistedPlayersPanel.add(playerEntry);
            }
        }

        whitelistedPlayersPanel.setPosAndSize(5, 23, width / 2, height - 24);
        whitelistedPlayersScroll.setPosAndSize(width / 2 - scrollWidth, 20, scrollWidth, height - 21);

        this.add(whitelistedPlayersPanel);
        this.add(whitelistedPlayersScroll);

        var playerList = Minecraft.getInstance().level.players();
        for (int i = 0; i < 10; i++) {
            for (AbstractClientPlayer player : playerList) {
                var playerEntry = getEntry(onlinePlayersPanel, player.getDisplayName().getString(), Icons.ADD);
                playerEntry.setWidth(this.width - 5);
                onlinePlayersPanel.add(playerEntry);
            }
        }

        onlinePlayersPanel.setPosAndSize(width / 2 + 5, 23, width / 2, height - 24);
        onlinePlayersScroll.setPosAndSize(width - scrollWidth, 20, scrollWidth, height - 21);
        this.add(onlinePlayersPanel);
        this.add(onlinePlayersScroll);
    }

    @NotNull
    private BlankPanel getEntry(Panel panel, String btnText, Icon icon) {
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

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        super.drawForeground(graphics, theme, x, y, w, h);
        theme.drawString(graphics, "Player Manager", x + w / 2, y + 7, SNOW_STORM_1, Theme.CENTERED);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_1.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 21, w - 2, 1);
    }


}

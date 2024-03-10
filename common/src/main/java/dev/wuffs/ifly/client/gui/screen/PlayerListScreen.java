package dev.wuffs.ifly.client.gui.screen;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class PlayerListScreen extends BaseScreen implements NordColors {

    Panel parentScreen;
    BlockPos blockPos;

    private BlankPanel panel;
    private PanelScrollBar scroll;

    public PlayerListScreen(Panel panel, BlockPos blockPos) {
        super();
        this.parentScreen = panel;
        this.blockPos = blockPos;
    }

    @Override
    public Panel getParent() {
        return this.parentScreen;
    }

    @Override
    public boolean onInit() {
        panel = new BlankPanel(this) {
            @Override
            public void alignWidgets() {
                align(WidgetLayout.VERTICAL);
            }
        };
        scroll = new PanelScrollBar(this, panel);

//        setWidth(getScreen().getGuiScaledWidth() - 80);
//        setHeight(getScreen().getGuiScaledHeight() - 80);
        setSizeProportional(0.70f, 0.7f);


        return super.onInit();
    }

    @Override
    public void alignWidgets() {
        align(WidgetLayout.VERTICAL);
        panel.setPosAndSize(20, 30, width, height - 20);
        panel.alignWidgets();
        scroll.setPosAndSize(width - 16, 20, 16, height - 20);
    }

    @Override
    public void addWidgets() {
        var playerList = Minecraft.getInstance().level.players();
        for (AbstractClientPlayer player : playerList) {
            var playerEntry = new BlankPanel(panel) {
                @Override
                public void alignWidgets() {
                    align(WidgetLayout.HORIZONTAL);
                }

                @Override
                public void addWidgets() {
                    this.add(new TextField(this).setText(player.getDisplayName()));
                    this.add(new SimpleButton(this, Component.empty(), Icons.ADD, (btn, mb) -> {}));
                }
            };
            playerEntry.setHeight(this.height - 20);
            playerEntry.setWidth(this.width);
            panel.add(playerEntry);
        }
        this.add(panel);
        this.add(scroll);
    }

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        super.drawForeground(graphics, theme, x, y, w, h);
        theme.drawString(graphics, "Player List", x + w / 2, y + 7, SNOW_STORM_1, Theme.CENTERED);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_1.draw(graphics, x + 1, y + 1, w - 2, h - 2);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 21, w - 2, 1);
    }
}

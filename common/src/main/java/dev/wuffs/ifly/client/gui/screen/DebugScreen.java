package dev.wuffs.ifly.client.gui.screen;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class DebugScreen extends BaseScreen implements NordColors {

    private Panel weMadeFlyingPanel;
    private Panel alredyFlyingPanel;
    private Button closeButton;

    private ObjectSet<UUID> weMadeFlying;
    private ObjectSet<UUID> alreadyFlying;

    public DebugScreen(ObjectSet<UUID> weMadeFlying, ObjectSet<UUID> alreadyFlying) {
        this.weMadeFlying = weMadeFlying;
        this.alreadyFlying = alreadyFlying;
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
        add(weMadeFlyingPanel = new WeMadeFlyingPanel());
        add(alredyFlyingPanel = new AlreadyFlyingPanel());
    }

    @Override
    public void alignWidgets() {
        closeButton.setPosAndSize(width - 20, 2, 16, 16);
        weMadeFlyingPanel.setPosAndSize(2, 20, width - 4, height / 2 - 30);
        alredyFlyingPanel.setPosAndSize(2, (height / 2) + 10, width - 4, height / 2 - 30);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.drawHollowRect(graphics, x, y, w, h, POLAR_NIGHT_0, true);
        POLAR_NIGHT_0.draw(graphics, x + 1, y + 1, w - 2, h - 2);
    }

    @Override
    public void drawForeground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawString(graphics, Component.literal("We made flying"), x + w / 2, y + 6, SNOW_STORM_1, Theme.CENTERED);
        theme.drawString(graphics, Component.literal("Already Flying"), x + w / 2, y + (h / 2) - 3, SNOW_STORM_1, Theme.CENTERED);
    }

    private class WeMadeFlyingPanel extends Panel {
        public WeMadeFlyingPanel() {
            super(DebugScreen.this);
        }

        @Override
        public void addWidgets() {
            weMadeFlying.forEach(uuid -> {
                add(new TextField(this).setText(Component.literal(uuid.toString())));
            });
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

    private class AlreadyFlyingPanel extends Panel {
        public AlreadyFlyingPanel() {
            super(DebugScreen.this);
        }

        @Override
        public void addWidgets() {
            alreadyFlying.forEach(uuid -> {
                add(new TextField(this).setText(Component.literal(uuid.toString())));
            });
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
}

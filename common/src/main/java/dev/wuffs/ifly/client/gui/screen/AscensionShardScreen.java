package dev.wuffs.ifly.client.gui.screen;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.icon.FaceIcon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.wuffs.ifly.common.PlayerLevel;
import dev.wuffs.ifly.network.C2SGUIInteract;
import dev.wuffs.ifly.network.Network;
import dev.wuffs.ifly.network.debug.C2SDebugScreen;
import dev.wuffs.ifly.network.records.AvailablePlayer;
import dev.wuffs.ifly.network.records.StoredPlayers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AscensionShardScreen extends BaseScreen implements NordColors {

    private BlankPanel whitelistedPlayersPanel;

    private Button infoButton;
    private Button addButton;

    public BlockPos blockPos;
    public List<StoredPlayers> storedPlayers;
    public List<AvailablePlayer> availablePlayers;

    public AscensionShardScreen(BlockPos blockPos, List<StoredPlayers> storedPlayers, List<AvailablePlayer> availablePlayers) {
        super();
        this.blockPos = blockPos;
        this.storedPlayers = storedPlayers;
        this.availablePlayers = availablePlayers;
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

            @Override
            public void onClicked(MouseButton button) {
                if(Screen.hasControlDown() && Screen.hasShiftDown()){
                    Network.CHANNEL.sendToServer(new C2SDebugScreen());
                }
            }
        });

        add(addButton = new SimpleButton(this, Component.literal("Add"), Icons.ADD.withTint(SNOW_STORM_2), (simpleButton, mouseButton) -> closeGui()) {
            @Override
            public void onClicked(MouseButton button) {
                new AddPlayerScreen(storedPlayers, availablePlayers, blockPos).openGui();
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

//        for (int i = 0; i < 30; i++) {
        for (StoredPlayers player : storedPlayers) {
            var playerEntry = getEntry(whitelistedPlayersPanel, player.player(), !player.level().isOwner());
            whitelistedPlayersPanel.add(playerEntry);
//            }
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

    private BlankPanel getEntry(Panel panel, GameProfile profile, boolean isEnabled) {
        var playerEntry = new BlankPanel(panel) {

            @Override
            public void addWidgets() {
                SimpleTextButton playerBtn = new NordButton(this, Component.literal(profile.getName()), FaceIcon.getFace(profile)) {
                    @Override
                    public void onClicked(MouseButton button) {
                        if (!isEnabled) {
                            return;
                        }
                        List<ContextMenuItem> contextMenuItems = new ArrayList<>();
//                        if (!profile.getId().equals(ownerUUID)) {
                        System.out.println("Player: " + profile.getName());
                        StoredPlayers currentP = storedPlayers.stream().filter(storedPlayer -> storedPlayer.player().getId().equals(Minecraft.getInstance().getGameProfile().getId())).findFirst().get();
                        StoredPlayers targetPlayer = storedPlayers.stream().filter(storedPlayer -> storedPlayer.player().getId().equals(profile.getId())).findFirst().get();
                        if (currentP.level().isManagerOrGreater()){
                            if(currentP.level().isOwner()){
                                contextMenuItems.add(new ContextMenuItem(Component.literal("Make owner"), Icons.DIAMOND, (b) -> {
                                    Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, profile, PlayerLevel.OWNER));
                                    storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                                    // Remove the current owner from the list and add them back as a normal user
                                    GameProfile currentOwner = storedPlayers.stream().filter(storedPlayer -> storedPlayer.level().isOwner()).findFirst().get().player();
                                    storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(currentOwner.getId()));
                                    storedPlayers.add(new StoredPlayers(currentOwner, PlayerLevel.MEMBER));

                                    // Add the new owner
                                    storedPlayers.add(new StoredPlayers(profile, PlayerLevel.OWNER));
                                }));
                            }

                            if (!targetPlayer.level().isManagerOrGreater()){
                                contextMenuItems.add(new ContextMenuItem(Component.literal("Make manger"), Icons.SHIELD, (b) -> {
                                    Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, profile, PlayerLevel.MANAGER));
                                    storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                                    storedPlayers.add(new StoredPlayers(profile, PlayerLevel.MANAGER));
                                }));
                            } else if (targetPlayer.level().isManagerOrGreater() && !targetPlayer.level().isOwner()){
                                contextMenuItems.add(new ContextMenuItem(Component.literal("Make member"), Icons.ACCEPT_GRAY, (b) -> {
                                    Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, profile, PlayerLevel.MEMBER));
                                    storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                                    storedPlayers.add(new StoredPlayers(profile, PlayerLevel.MEMBER));
                                }));

                            }
                            contextMenuItems.add(new ContextMenuItem(Component.literal("Remove"), Icons.CLOSE, (b) -> {
                                Network.CHANNEL.sendToServer(new C2SGUIInteract(blockPos, profile, PlayerLevel.REMOVE));
                                storedPlayers.removeIf(storedPlayer -> storedPlayer.player().getId().equals(profile.getId()));
                                AscensionShardScreen.this.refreshWidgets();
                            }));
                        }
                        if (!contextMenuItems.isEmpty()) {
                            List<ContextMenuItem> contextMenu = new ArrayList<>(List.of(
                                    new ContextMenuItem(Component.literal(profile.getName()), FaceIcon.getFace(profile), null).setCloseMenu(false),
                                    ContextMenuItem.SEPARATOR
                            ));

                            contextMenu.addAll(contextMenuItems);
                            getGui().openContextMenu(contextMenu);
                        }
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
        StoredPlayers storedPlayer = storedPlayers.stream().filter(sp -> sp.level().equals(PlayerLevel.OWNER)).findFirst().get();
        list.add(Component.literal("Owner: " + storedPlayer.player().getName()).withStyle(ChatFormatting.AQUA));
        list.add(Component.literal("UUID: " + storedPlayer.player().getId()).withStyle(ChatFormatting.YELLOW));
    }
}

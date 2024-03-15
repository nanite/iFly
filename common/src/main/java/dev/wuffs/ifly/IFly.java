package dev.wuffs.ifly;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.blocks.Blocks;
import dev.wuffs.ifly.items.Items;
import dev.wuffs.ifly.network.Network;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class IFly {
    public static final String MOD_ID = "ifly";

    public static void init(){
        Blocks.BLOCKS.register();
        Blocks.BLOCKENTITY.register();
        Network.register();

//        ClientLifecycleEvent.CLIENT_SETUP.register(IFlyClient::setup);
        Items.ITEMS.register();
        CREATIVE_TAB.register();

        BlockEvent.BREAK.register((world, pos, state, player, xp) -> {
            if (world == null || world.getServer() == null || world.isClientSide){
                return EventResult.pass();
            };

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AscensionShardBlockEntity asBlockEntity) {
                if (player.hasPermissions(Commands.LEVEL_GAMEMASTERS) || asBlockEntity.storedPlayers.stream().anyMatch(storedPlayer -> storedPlayer.player().getId().equals(player.getUUID()) && storedPlayer.level().isManagerOrGreater())) {
                    return EventResult.pass();
                }else {
                    player.displayClientMessage(Component.literal("You are not the owner/manager of this block!").withStyle(ChatFormatting.RED), true);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(IFly.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> TAB = CREATIVE_TAB.register(IFly.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.ifly.creative_tab"))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        Items.ITEMS.forEach(e -> output.accept(e.get()));
                    })
            .icon(() -> new ItemStack(Items.BUNDLE.get())).build());
}

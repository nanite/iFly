package dev.wuffs.ifly;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.wuffs.ifly.blocks.Blocks;
import dev.wuffs.ifly.client.AscensionShardClient;
import dev.wuffs.ifly.common.BlockEventListener;
import dev.wuffs.ifly.common.PlayerEventListener;
import dev.wuffs.ifly.flight.FlightManager;
import dev.wuffs.ifly.integration.ModIntegrations;
import dev.wuffs.ifly.items.Items;
import dev.wuffs.ifly.network.Network;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class AscensionShard {
    public static final String MOD_ID = "ifly";

    public AscensionShard(){
        ModIntegrations.init();

        CommandRegistrationEvent.EVENT.register(this::registerCommands);
        Blocks.BLOCKS.register();
        Blocks.BLOCKENTITY.register();
        Items.ITEMS.register();
        CREATIVE_TAB.register();

        Network.register();

        PlayerEvent.PLAYER_QUIT.register(PlayerEventListener::onPlayerQuitEvent);
        BlockEvent.BREAK.register(BlockEventListener::onBlockBreakEvent);

        TickEvent.Server.SERVER_POST.register(FlightManager.INSTANCE::tick);
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection selection) {
        new TempCommands().register(dispatcher);
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(AscensionShard.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> TAB = CREATIVE_TAB.register(AscensionShard.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.ifly.creative_tab"))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        Items.ITEMS.forEach(e -> output.accept(e.get()));
                    })
            .icon(() -> new ItemStack(Items.BUNDLE.get())).build());

    public void setup(){
//        GeckoLib.initialize();
    }

    public static ResourceLocation rl(String value) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, value);
    }
}

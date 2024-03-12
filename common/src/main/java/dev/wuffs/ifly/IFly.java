package dev.wuffs.ifly;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.wuffs.ifly.blocks.Blocks;
import dev.wuffs.ifly.items.Items;
import dev.wuffs.ifly.network.Network;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class IFly {
    public static final String MOD_ID = "ifly";

    public static void init(){
        Blocks.BLOCKS.register();
        Blocks.BLOCKENTITY.register();
        Network.register();

//        ClientLifecycleEvent.CLIENT_SETUP.register(IFlyClient::setup);
        Items.ITEMS.register();
        CREATIVE_TAB.register();
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

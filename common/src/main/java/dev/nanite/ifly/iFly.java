package dev.nanite.ifly;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.nanite.ifly.item.Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class iFly {
    public static final String MOD_ID = "ifly";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(iFly.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> TAB = CREATIVE_TAB.register(iFly.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.ifly.creative_tab"))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        Items.ITEMS.forEach(e -> output.accept(e.get()));
                    })
            .icon(() -> new ItemStack(Items.FLY_ITEM.get())).build());
    
    public static void init() {
        Items.ITEMS.register();
        
        System.out.println(iFlyExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}

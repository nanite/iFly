package dev.nanite.ifly.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.nanite.ifly.iFly;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class Items {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(iFly.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> FLY_ITEM = ITEMS.register("fly_item", FlyItem::new);

}

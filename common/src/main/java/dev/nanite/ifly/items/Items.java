package dev.nanite.ifly.items;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.nanite.ifly.iFly;
import dev.nanite.ifly.trims.FlyTrim;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;

public class Items {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(iFly.MOD_ID, Registries.ITEM);
    public static RegistrySupplier<Item> FLY_ITEM = ITEMS.register("fly_item", () -> SmithingTemplateItem.createArmorTrimTemplate(FlyTrim.FLY_TRIM));
}

package dev.wuffs.ifly.items;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.wuffs.ifly.IFly;
import dev.wuffs.ifly.blocks.Blocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(IFly.MOD_ID, Registries.ITEM);

    private static RegistrySupplier<Item> blockItem(String id, Supplier<Block> b) {
        return ITEMS.register(id, () -> new BlockItem(b.get(), new Item.Properties()));
    }

    public static final RegistrySupplier<Item> BUNDLE = blockItem("tbd", Blocks.TBD);
}

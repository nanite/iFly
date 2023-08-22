package dev.nanite.ifly.trims;

import dev.nanite.ifly.iFly;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimPattern;

import static dev.nanite.ifly.items.Items.FLY_ITEM;

public class FlyTrim {
    public static final ResourceKey<TrimPattern> FLY_TRIM = ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation(iFly.MOD_ID, "fly_trim"));

    public static void bootstrap(BootstapContext<TrimPattern> bootstapContext) {
        register(bootstapContext, FLY_ITEM.get(), FLY_TRIM);
    }

    private static void register(BootstapContext<TrimPattern> p_267064_, Item p_267097_, ResourceKey<TrimPattern> p_267079_) {
        TrimPattern trimpattern = new TrimPattern(p_267079_.location(), BuiltInRegistries.ITEM.wrapAsHolder(p_267097_), Component.translatable(Util.makeDescriptionId("trim_pattern", p_267079_.location())));
        p_267064_.register(p_267079_, trimpattern);
    }
}

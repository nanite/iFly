package dev.wuffs.ifly.fabric;

import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.blocks.Blocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {

    public RecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {

//        Bamboo torch
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.ASHARD.get())
                .unlockedBy("has_netherstar", has(Items.NETHER_STAR))
                .pattern("SES")
                .pattern("ONO")
                .pattern("OOO")
                .define('S', Items.NETHER_STAR)
                .define('E', Items.ELYTRA)
                .define('O', Items.OBSIDIAN)
                .define('N', Items.NETHERITE_INGOT)
                .save(exporter, ResourceLocation.fromNamespaceAndPath(AscensionShard.MOD_ID, "ascension_shard"));
    }
}

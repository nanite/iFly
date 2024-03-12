package dev.wuffs.ifly.fabric;

import dev.wuffs.ifly.IFly;
import dev.wuffs.ifly.blocks.Blocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput output) {
        super(output);
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
                .save(exporter, new ResourceLocation(IFly.MOD_ID, "ascension_shard"));
    }
}
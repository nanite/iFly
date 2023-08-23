package dev.nanite.ifly.fabric;

import dev.nanite.ifly.iFly;
import dev.nanite.ifly.items.Items;
import dev.nanite.ifly.trims.FlyTrim;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DataGens implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(DRP::new);
        pack.addProvider(FlyTags::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.TRIM_PATTERN, FlyTrim::bootstrap);
    }

    public class RecipeGenerator extends FabricRecipeProvider {
        public RecipeGenerator(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void buildRecipes(Consumer<FinishedRecipe> exporter) {
            trimSmithing(exporter, Items.FLY_ITEM.get(),new ResourceLocation(iFly.MOD_ID, "fly_item"));
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Items.FLY_ITEM.get())
                    .unlockedBy("has_netherstar", has(net.minecraft.world.item.Items.NETHER_STAR))
                    .pattern("SFS")
                    .pattern("SWS")
                    .pattern("FFF")
                    .define('F', net.minecraft.world.item.Items.FEATHER)
                    .define('W', net.minecraft.world.item.Items.WITHER_SKELETON_SKULL)
                    .define('S', net.minecraft.world.item.Items.NETHER_STAR)
                    .save(exporter, new ResourceLocation(iFly.MOD_ID, "fly_item_crafting"));
        }
    }

    public class FlyTags extends FabricTagProvider.ItemTagProvider {

        public FlyTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES).add(Items.FLY_ITEM.get());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public class DRP extends FabricDynamicRegistryProvider {
        public DRP(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            entries.addAll(registries.lookupOrThrow(Registries.TRIM_PATTERN));
        }

        @Override
        public String getName() {
            return "iFly Data";
        }
    }
}

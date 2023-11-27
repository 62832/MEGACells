package gripe._90.megacells.datagen;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;

import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.registries.GeneratorsBlocks;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.AppMekItems;

public class ForgeRecipeProvider extends RecipeProvider {
    private static final TagKey<Item> OSMIUM = ItemTags.create(new ResourceLocation("forge", "ingots/osmium"));

    public ForgeRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            appmekRecipe(
                    consumer,
                    MEGACells.makeId("cells/mega_chemical_cell_housing"),
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                            .pattern("aba")
                            .pattern("b b")
                            .pattern("ddd")
                            .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                            .define('b', AEItems.SKY_DUST)
                            .define('d', OSMIUM)
                            .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST)));

            chemCell(consumer, AppMekItems.CHEMICAL_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
            chemCell(consumer, AppMekItems.CHEMICAL_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
            chemCell(consumer, AppMekItems.CHEMICAL_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
            chemCell(consumer, AppMekItems.CHEMICAL_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
            chemCell(consumer, AppMekItems.CHEMICAL_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

            chemPortable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
            chemPortable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
            chemPortable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
            chemPortable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
            chemPortable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

            appmekRecipe(
                    consumer,
                    MEGACells.makeId("crafting/radioactive_cell_component"),
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.RADIOACTIVE_CELL_COMPONENT)
                            .pattern("aba")
                            .pattern("cdc")
                            .pattern("aea")
                            .define('a', AEItems.SKY_DUST)
                            .define('b', MEGAItems.ACCUMULATION_PROCESSOR)
                            .define('c', MekanismBlocks.RADIOACTIVE_WASTE_BARREL)
                            .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                            .define('e', AEItems.CELL_COMPONENT_256K)
                            .unlockedBy("has_cell_component_256k", has(AEItems.CELL_COMPONENT_256K))
                            .unlockedBy("has_waste_barrel", has(MekanismBlocks.RADIOACTIVE_WASTE_BARREL)));
            appmekRecipe(
                    consumer,
                    MEGACells.makeId("cells/standard/radioactive_chemical_cell"),
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.RADIOACTIVE_CHEMICAL_CELL)
                            .pattern("aba")
                            .pattern("bcb")
                            .pattern("ded")
                            .define('a', GeneratorsBlocks.REACTOR_GLASS)
                            .define('b', AEItems.SKY_DUST)
                            .define('c', AppMekItems.RADIOACTIVE_CELL_COMPONENT)
                            .define('d', MekanismItems.HDPE_SHEET)
                            .define('e', MekanismItems.POLONIUM_PELLET)
                            .unlockedBy("has_radioactive_cell_component", has(AppMekItems.RADIOACTIVE_CELL_COMPONENT)));
        }
    }

    private void chemCell(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        appmekRecipe(
                consumer,
                MEGACells.makeId("cells/standard/" + cell.id().getPath()),
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cell)
                        .pattern("aba")
                        .pattern("bcb")
                        .pattern("ddd")
                        .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                        .define('b', AEItems.SKY_DUST)
                        .define('c', component)
                        .define('d', OSMIUM)
                        .unlockedBy("has_" + component.id().getPath(), has(component)));
        appmekRecipe(
                consumer,
                MEGACells.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"),
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                        .requires(component)
                        .unlockedBy("has_" + component.id().getPath(), has(component))
                        .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)));
    }

    private void chemPortable(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        appmekRecipe(
                consumer,
                MEGACells.makeId("cells/portable/" + cell.id().getPath()),
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(AEBlocks.CHEST)
                        .requires(component)
                        .requires(AEBlocks.DENSE_ENERGY_CELL)
                        .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                        .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING))
                        .unlockedBy("has_" + component.id().getPath(), has(component))
                        .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL)));
    }

    private void appmekRecipe(Consumer<FinishedRecipe> consumer, ResourceLocation id, RecipeBuilder builder) {
        MEGACells.PLATFORM.addIntegrationRecipe(consumer, builder, Addons.APPMEK, id);
    }
}

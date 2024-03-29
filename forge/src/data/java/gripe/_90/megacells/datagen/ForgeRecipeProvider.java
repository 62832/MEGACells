package gripe._90.megacells.datagen;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
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
import gripe._90.megacells.integration.arseng.ArsEngItems;

public class ForgeRecipeProvider extends RecipeProvider {
    private static final TagKey<Item> OSMIUM = ItemTags.create(new ResourceLocation("forge", "ingots/osmium"));

    public ForgeRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            MEGACells.PLATFORM.addIntegrationRecipe(
                    writer,
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                            .pattern("aba")
                            .pattern("b b")
                            .pattern("ddd")
                            .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                            .define('b', AEItems.SKY_DUST)
                            .define('d', OSMIUM)
                            .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST)),
                    Addons.APPMEK,
                    MEGACells.makeId("cells/mega_chemical_cell_housing"));

            chemCell(writer, AppMekItems.CHEMICAL_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
            chemCell(writer, AppMekItems.CHEMICAL_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
            chemCell(writer, AppMekItems.CHEMICAL_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
            chemCell(writer, AppMekItems.CHEMICAL_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
            chemCell(writer, AppMekItems.CHEMICAL_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

            chemPortable(writer, AppMekItems.PORTABLE_CHEMICAL_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
            chemPortable(writer, AppMekItems.PORTABLE_CHEMICAL_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
            chemPortable(writer, AppMekItems.PORTABLE_CHEMICAL_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
            chemPortable(writer, AppMekItems.PORTABLE_CHEMICAL_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
            chemPortable(writer, AppMekItems.PORTABLE_CHEMICAL_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

            MEGACells.PLATFORM.addIntegrationRecipe(
                    writer,
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
                            .unlockedBy("has_waste_barrel", has(MekanismBlocks.RADIOACTIVE_WASTE_BARREL)),
                    Addons.APPMEK,
                    MEGACells.makeId("crafting/radioactive_cell_component"));
            MEGACells.PLATFORM.addIntegrationRecipe(
                    writer,
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.RADIOACTIVE_CHEMICAL_CELL)
                            .pattern("aba")
                            .pattern("bcb")
                            .pattern("ded")
                            .define('a', GeneratorsBlocks.REACTOR_GLASS)
                            .define('b', AEItems.SKY_DUST)
                            .define('c', AppMekItems.RADIOACTIVE_CELL_COMPONENT)
                            .define('d', MekanismItems.HDPE_SHEET)
                            .define('e', MekanismItems.POLONIUM_PELLET)
                            .unlockedBy("has_radioactive_cell_component", has(AppMekItems.RADIOACTIVE_CELL_COMPONENT)),
                    Addons.APPMEK,
                    MEGACells.makeId("cells/standard/radioactive_chemical_cell"));
        }

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.ARSENG)) {
            sourceCell(writer, ArsEngItems.SOURCE_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
            sourceCell(writer, ArsEngItems.SOURCE_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
            sourceCell(writer, ArsEngItems.SOURCE_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
            sourceCell(writer, ArsEngItems.SOURCE_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
            sourceCell(writer, ArsEngItems.SOURCE_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

            sourcePortable(writer, ArsEngItems.PORTABLE_SOURCE_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
            sourcePortable(writer, ArsEngItems.PORTABLE_SOURCE_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
            sourcePortable(writer, ArsEngItems.PORTABLE_SOURCE_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
            sourcePortable(writer, ArsEngItems.PORTABLE_SOURCE_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
            sourcePortable(writer, ArsEngItems.PORTABLE_SOURCE_CELL_256M, MEGAItems.CELL_COMPONENT_256M);
        }
    }

    private void chemCell(Consumer<FinishedRecipe> writer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cell)
                        .pattern("aba")
                        .pattern("bcb")
                        .pattern("ddd")
                        .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                        .define('b', AEItems.SKY_DUST)
                        .define('c', component)
                        .define('d', OSMIUM)
                        .unlockedBy("has_" + component.id().getPath(), has(component)),
                Addons.APPMEK,
                MEGACells.makeId("cells/standard/" + cell.id().getPath()));
        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                        .requires(component)
                        .unlockedBy("has_" + component.id().getPath(), has(component))
                        .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)),
                Addons.APPMEK,
                MEGACells.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));
    }

    private void chemPortable(Consumer<FinishedRecipe> writer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(AEBlocks.CHEST)
                        .requires(component)
                        .requires(AEBlocks.DENSE_ENERGY_CELL)
                        .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                        .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING))
                        .unlockedBy("has_" + component.id().getPath(), has(component))
                        .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL)),
                Addons.APPMEK,
                MEGACells.makeId("cells/portable/" + cell.id().getPath()));
    }

    private void sourceCell(Consumer<FinishedRecipe> writer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(ArsEngItems.MEGA_SOURCE_CELL_HOUSING)
                        .requires(component)
                        .unlockedBy("has_" + component.id().getPath(), has(component))
                        .unlockedBy("has_mega_source_cell_housing", has(ArsEngItems.MEGA_SOURCE_CELL_HOUSING)),
                Addons.ARSENG,
                MEGACells.makeId("cells/standard/" + cell.id().getPath()));
    }

    private void sourcePortable(Consumer<FinishedRecipe> writer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(AEBlocks.CHEST)
                        .requires(component)
                        .requires(AEBlocks.DENSE_ENERGY_CELL)
                        .requires(ArsEngItems.MEGA_SOURCE_CELL_HOUSING)
                        .unlockedBy("has_mega_source_cell_housing", has(ArsEngItems.MEGA_SOURCE_CELL_HOUSING))
                        .unlockedBy("has_" + component.id().getPath(), has(component))
                        .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL)),
                Addons.ARSENG,
                MEGACells.makeId("cells/portable/" + cell.id().getPath()));
    }
}

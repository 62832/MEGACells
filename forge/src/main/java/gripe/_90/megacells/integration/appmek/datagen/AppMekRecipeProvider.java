package gripe._90.megacells.integration.appmek.datagen;

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
import net.minecraft.world.item.crafting.Ingredient;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;

import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.registries.GeneratorsBlocks;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.util.Utils;

public class AppMekRecipeProvider extends RecipeProvider {
    public AppMekRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', ItemTags.create(new ResourceLocation("forge", "ingots/osmium")))
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, Utils.makeId("cells/mega_chemical_cell_housing"));

        cell(consumer, AppMekItems.CHEMICAL_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
        cell(consumer, AppMekItems.CHEMICAL_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
        cell(consumer, AppMekItems.CHEMICAL_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
        cell(consumer, AppMekItems.CHEMICAL_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
        cell(consumer, AppMekItems.CHEMICAL_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

        portable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_1M, MEGAItems.CELL_COMPONENT_1M);
        portable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_4M, MEGAItems.CELL_COMPONENT_4M);
        portable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_16M, MEGAItems.CELL_COMPONENT_16M);
        portable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_64M, MEGAItems.CELL_COMPONENT_64M);
        portable(consumer, AppMekItems.PORTABLE_CHEMICAL_CELL_256M, MEGAItems.CELL_COMPONENT_256M);

        InscriberRecipeBuilder.inscribe(AEItems.SINGULARITY, AppMekItems.RADIOACTIVE_CELL_COMPONENT, 1)
                .setMode(InscriberProcessType.PRESS)
                .setTop(Ingredient.of(AEItems.CELL_COMPONENT_256K))
                .setBottom(Ingredient.of(MekanismBlocks.RADIOACTIVE_WASTE_BARREL))
                .save(consumer, Utils.makeId("inscriber/radioactive_cell_component"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.RADIOACTIVE_CHEMICAL_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', GeneratorsBlocks.REACTOR_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', AppMekItems.RADIOACTIVE_CELL_COMPONENT)
                .define('d', MekanismItems.HDPE_SHEET)
                .define('e', MekanismItems.POLONIUM_PELLET)
                .unlockedBy("has_radioactive_cell_component", has(AppMekItems.RADIOACTIVE_CELL_COMPONENT))
                .save(consumer, Utils.makeId("cells/standard/radioactive_chemical_cell"));
    }

    private void cell(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cell)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', ItemTags.create(new ResourceLocation("forge", "ingots/osmium")))
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .save(consumer, Utils.makeId("cells/standard/" + cell.id().getPath()));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                .requires(component)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING))
                .save(consumer, Utils.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));
    }

    private void portable(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> component) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(AEBlocks.CHEST)
                .requires(component)
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, Utils.makeId("cells/portable/" + cell.id().getPath()));
    }

    /*
     * why the fuck is this final now
     * 
     * @Override public @NotNull String getName() { return super.getName() + "/appmek"; }
     */
}

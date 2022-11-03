package gripe._90.megacells.integration.appmek.datagen;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;

import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.registries.GeneratorsBlocks;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.appmek.item.AppMekItems;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

public class AppMekRecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public AppMekRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', ItemTags.create(new ResourceLocation("forge", "ingots/osmium")))
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, MEGACells.makeId("cells/mega_chemical_cell_housing"));

        for (var storage : AppMekCellType.CHEMICAL.getCells()) {
            cell(consumer, storage);
        }
        for (var portable : AppMekCellType.CHEMICAL.getPortableCells()) {
            portable(consumer, portable);
        }

        ShapedRecipeBuilder.shaped(AppMekItems.RADIOACTIVE_CHEMICAL_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', GeneratorsBlocks.REACTOR_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', AppMekItems.RADIOACTIVE_CELL_COMPONENT)
                .define('d', MekanismItems.HDPE_SHEET)
                .define('e', MekanismItems.POLONIUM_PELLET)
                .unlockedBy("has_radioactive_cell_component", has(AppMekItems.RADIOACTIVE_CELL_COMPONENT))
                .save(consumer, MEGACells.makeId("cells/standard/radioactive_chemical_cell"));
    }

    private void cell(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cellDefinition) {
        var cell = (MEGAStorageCell) cellDefinition.asItem();
        var component = cell.getTier().componentSupplier().get();

        var componentPath = Registry.ITEM.getKey(component).getPath();
        var cellPath = cellDefinition.id().getPath();

        ShapedRecipeBuilder.shaped(cellDefinition)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', ItemTags.create(new ResourceLocation("forge", "ingots/osmium")))
                .unlockedBy("has_" + componentPath, has(component))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath));
        ShapelessRecipeBuilder.shapeless(cellDefinition)
                .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                .requires(component)
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath + "_with_housing"));
    }

    private void portable(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cellDefinition) {
        var portableCell = (MEGAPortableCell) cellDefinition.asItem();
        ShapelessRecipeBuilder.shapeless(portableCell)
                .requires(AEBlocks.CHEST)
                .requires(portableCell.getTier().componentSupplier().get())
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                .unlockedBy("has_mega_chemical_cell_housing", has(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("cells/portable/" + cellDefinition.id().getPath()));
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "/appmek";
    }
}

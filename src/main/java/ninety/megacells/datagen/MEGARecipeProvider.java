package ninety.megacells.datagen;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;

import ninety.megacells.MEGACells;
import ninety.megacells.block.MEGABlocks;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.IMEGACellType;
import ninety.megacells.item.MEGACellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;
import ninety.megacells.util.MEGATier;

public class MEGARecipeProvider extends RecipeProvider {
    public MEGARecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        component(consumer, MEGATier._1M, AEItems.SKY_DUST.asItem());
        component(consumer, MEGATier._4M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGATier._16M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGATier._64M, AEItems.SINGULARITY.asItem());
        component(consumer, MEGATier._256M, AEItems.SINGULARITY.asItem());

        housing(consumer, MEGACellType.ITEM);
        housing(consumer, MEGACellType.FLUID);

        for (var storage : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                ChemicalCellType.TYPE.getCells().stream()).flatMap(s -> s).toList()) {
            cell(consumer, storage);
        }
        for (var portable : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream(),
                ChemicalCellType.TYPE.getPortableCells().stream()).flatMap(s -> s).toList()) {
            portable(consumer, portable);
        }

        ShapedRecipeBuilder.shaped(MEGABlocks.MEGA_CRAFTING_UNIT)
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', ConventionTags.IRON_INGOT)
                .define('b', AEItems.LOGIC_PROCESSOR)
                .define('c', AEParts.SMART_CABLE.item(AEColor.TRANSPARENT))
                .define('d', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("has_logic_processor", has(AEItems.LOGIC_PROCESSOR))
                .save(consumer, MEGACells.makeId("crafting/mega_crafting_unit"));
        craftingBlock(consumer, MEGABlocks.CRAFTING_ACCELERATOR, AEItems.ENGINEERING_PROCESSOR);
        craftingBlock(consumer, MEGABlocks.CRAFTING_STORAGE_1M, MEGAItems.CELL_COMPONENT_1M);
        craftingBlock(consumer, MEGABlocks.CRAFTING_STORAGE_4M, MEGAItems.CELL_COMPONENT_4M);
        craftingBlock(consumer, MEGABlocks.CRAFTING_STORAGE_16M, MEGAItems.CELL_COMPONENT_16M);
        craftingBlock(consumer, MEGABlocks.CRAFTING_STORAGE_64M, MEGAItems.CELL_COMPONENT_64M);
        craftingBlock(consumer, MEGABlocks.CRAFTING_STORAGE_256M, MEGAItems.CELL_COMPONENT_256M);
        craftingBlock(consumer, MEGABlocks.CRAFTING_MONITOR, AEParts.STORAGE_MONITOR);
    }

    private void component(Consumer<FinishedRecipe> consumer, MEGATier tier, Item binder) {
        var preceding = tier == MEGATier._1M ? AEItems.CELL_COMPONENT_256K.asItem()
                : MEGATier.values()[tier.index - 2].getComponent();

        ShapedRecipeBuilder.shaped(tier.getComponent())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca")
                .define('a', binder)
                .define('b', AEItems.CALCULATION_PROCESSOR)
                .define('c', preceding)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_" + MEGAItems.getItemPath(preceding), has(preceding))
                .save(consumer, MEGACells.makeId(MEGAItems.getItemPath(tier.getComponent())));
    }

    private void cell(Consumer<FinishedRecipe> consumer, Item cellItem) {
        var cell = (MEGAStorageCell) cellItem;

        var component = cell.getTier().getComponent();
        var housing = cell.getType().housing();
        var housingMaterial = cell.getType().housingMaterial();

        var componentPath = MEGAItems.getItemPath(component);
        var cellPath = MEGAItems.getItemPath(cellItem);

        ShapedRecipeBuilder.shaped(cellItem)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + componentPath, has(component))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath));
        ShapelessRecipeBuilder.shapeless(cellItem)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_" + MEGAItems.getItemPath(housing), has(housing))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath + "_with_housing"));
    }

    private void portable(Consumer<FinishedRecipe> consumer, Item portableCellItem) {
        var portableCell = (MEGAPortableCell) portableCellItem;
        var housing = portableCell.type.housing();
        ShapelessRecipeBuilder.shapeless(portableCell)
                .requires(AEBlocks.CHEST)
                .requires(portableCell.tier.getComponent())
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + MEGAItems.getItemPath(housing), has(housing))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("cells/portable/" + MEGAItems.getItemPath(portableCell)));
    }

    private void housing(Consumer<FinishedRecipe> consumer, IMEGACellType type) {
        var housing = type.housing();
        ShapedRecipeBuilder.shaped(type.housing())
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', type.housingMaterial())
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, MEGACells.makeId("cells/" + MEGAItems.getItemPath(housing)));
    }

    private void craftingBlock(Consumer<FinishedRecipe> consumer, MEGABlocks.BlockDefinition<?> unit, ItemLike part) {
        ShapelessRecipeBuilder.shapeless(unit)
                .requires(MEGABlocks.MEGA_CRAFTING_UNIT)
                .requires(part)
                .unlockedBy("has_mega_crafting_unit", has(MEGABlocks.MEGA_CRAFTING_UNIT))
                .save(consumer, MEGACells.makeId("crafting/" + MEGAItems.getItemPath(unit.asItem())));
    }
}

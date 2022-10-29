package gripe._90.megacells.datagen;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.items.storage.StorageTier;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;
import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.item.cell.IMEGACellType;
import gripe._90.megacells.item.cell.MEGACellType;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

public class MEGARecipeProvider extends RecipeProvider {
    public MEGARecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        component(consumer, MEGAItems.TIER_1M, AEItems.SKY_DUST.asItem());
        component(consumer, MEGAItems.TIER_4M, AEItems.ENDER_DUST.asItem());
        component(consumer, MEGAItems.TIER_16M, AEItems.ENDER_DUST.asItem());
        component(consumer, MEGAItems.TIER_64M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGAItems.TIER_256M, AEItems.MATTER_BALL.asItem());

        housing(consumer, MEGACellType.ITEM);
        housing(consumer, MEGACellType.FLUID);
        housing(consumer, AppMekCellType.CHEMICAL);

        for (var storage : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                AppMekCellType.CHEMICAL.getCells().stream()).flatMap(s -> s).toList()) {
            cell(consumer, storage);
        }
        for (var portable : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream(),
                AppMekCellType.CHEMICAL.getPortableCells().stream()).flatMap(s -> s).toList()) {
            portable(consumer, portable);
        }

        ShapedRecipeBuilder.shaped(MEGAItems.BULK_ITEM_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', MEGAItems.BULK_CELL_COMPONENT)
                .define('d', Items.NETHERITE_INGOT)
                .unlockedBy("has_bulk_cell_component", has(MEGAItems.BULK_CELL_COMPONENT))
                .save(consumer, MEGACells.makeId("cells/standard/bulk_item_cell"));

        ShapedRecipeBuilder.shaped(MEGABlocks.MEGA_ENERGY_CELL)
                .pattern("aaa")
                .pattern("aba")
                .pattern("aaa")
                .define('a', AEBlocks.DENSE_ENERGY_CELL)
                .define('b', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .unlockedBy("has_engineering_processor", has(AEItems.ENGINEERING_PROCESSOR))
                .save(consumer, MEGACells.makeId("mega_energy_cell"));

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

        ShapelessRecipeBuilder.shapeless(MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("crafting/greater_energy_card"));
        ShapelessRecipeBuilder.shapeless(MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ENERGY_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("crafting/greater_energy_card_upgraded"));
    }

    private void component(Consumer<FinishedRecipe> consumer, StorageTier tier, ItemLike binder) {
        var preceding = tier == MEGAItems.TIER_1M ? AEItems.CELL_COMPONENT_256K.asItem()
                : MEGAItems.getTiers().get(tier.index() - 2).componentSupplier().get();

        ShapedRecipeBuilder.shaped(tier.componentSupplier().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca")
                .define('a', binder)
                .define('b', AEItems.CALCULATION_PROCESSOR)
                .define('c', preceding)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_" + MEGACells.getItemPath(preceding), has(preceding))
                .save(consumer, MEGACells.makeId(MEGACells.getItemPath(tier.componentSupplier().get())));
    }

    private void cell(Consumer<FinishedRecipe> consumer, Item cellItem) {
        var cell = (MEGAStorageCell) cellItem;

        var component = cell.getTier().componentSupplier().get();
        var housing = cell.getType().housing();
        var housingMaterial = cell.getType().housingMaterial();

        var componentPath = MEGACells.getItemPath(component);
        var cellPath = MEGACells.getItemPath(cellItem);

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
                .unlockedBy("has_" + MEGACells.getItemPath(housing), has(housing))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath + "_with_housing"));
    }

    private void portable(Consumer<FinishedRecipe> consumer, Item portableCellItem) {
        var portableCell = (MEGAPortableCell) portableCellItem;
        var housing = portableCell.getType().housing();
        ShapelessRecipeBuilder.shapeless(portableCell)
                .requires(AEBlocks.CHEST)
                .requires(portableCell.getTier().componentSupplier().get())
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + MEGACells.getItemPath(housing), has(housing))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("cells/portable/" + MEGACells.getItemPath(portableCell)));
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
                .save(consumer, MEGACells.makeId("cells/" + MEGACells.getItemPath(housing)));
    }

    private void craftingBlock(Consumer<FinishedRecipe> consumer, MEGABlocks.BlockDefinition<?> unit, ItemLike part) {
        ShapelessRecipeBuilder.shapeless(unit)
                .requires(MEGABlocks.MEGA_CRAFTING_UNIT)
                .requires(part)
                .unlockedBy("has_mega_crafting_unit", has(MEGABlocks.MEGA_CRAFTING_UNIT))
                .save(consumer, MEGACells.makeId("crafting/" + MEGACells.getItemPath(unit.asItem())));
    }
}

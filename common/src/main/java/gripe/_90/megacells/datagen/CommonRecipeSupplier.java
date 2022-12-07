package gripe._90.megacells.datagen;

import java.util.function.Consumer;
import java.util.stream.Stream;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.items.storage.StorageTier;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.IMEGACellType;
import gripe._90.megacells.item.cell.MEGACellType;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

public class CommonRecipeSupplier {

    public static final CommonRecipeSupplier INSTANCE = new CommonRecipeSupplier();

    private CommonRecipeSupplier() {
    }

    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        component(consumer, MEGAItems.TIER_1M, StorageTier.SIZE_256K, AEItems.SKY_DUST.asItem());
        component(consumer, MEGAItems.TIER_4M, MEGAItems.TIER_1M, AEItems.ENDER_DUST.asItem());
        component(consumer, MEGAItems.TIER_16M, MEGAItems.TIER_4M, AEItems.ENDER_DUST.asItem());
        component(consumer, MEGAItems.TIER_64M, MEGAItems.TIER_16M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGAItems.TIER_256M, MEGAItems.TIER_64M, AEItems.MATTER_BALL.asItem());

        housing(consumer, MEGACellType.ITEM);
        housing(consumer, MEGACellType.FLUID);

        for (var storage : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream()).flatMap(s -> s).toList()) {
            cell(consumer, storage);
        }
        for (var portable : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream()).flatMap(s -> s).toList()) {
            portable(consumer, portable);
        }

        specialisedComponent(consumer, MEGAItems.CELL_COMPONENT_16M, AEItems.SPATIAL_16_CELL_COMPONENT,
                MEGAItems.BULK_CELL_COMPONENT);
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
                .save(consumer, MEGACells.makeId("crafting/mega_energy_cell"));

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

    private void component(Consumer<FinishedRecipe> consumer, StorageTier tier, StorageTier preceding,
            ItemLike binder) {
        var precedingComponent = preceding.componentSupplier().get();
        ShapedRecipeBuilder.shaped(tier.componentSupplier().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca")
                .define('a', binder)
                .define('b', AEItems.CALCULATION_PROCESSOR)
                .define('c', precedingComponent)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_" + Registry.ITEM.getKey(precedingComponent).getPath(), has(precedingComponent))
                .save(consumer,
                        MEGACells.makeId("cells/" + Registry.ITEM.getKey(tier.componentSupplier().get()).getPath()));
    }

    private void specialisedComponent(Consumer<FinishedRecipe> consumer, ItemLike top, ItemLike bottom,
            ItemDefinition<?> output) {
        InscriberRecipeBuilder.inscribe(AEItems.SINGULARITY, output, 1)
                .setMode(InscriberProcessType.PRESS)
                .setTop(Ingredient.of(top)).setBottom(Ingredient.of(bottom))
                .save(consumer, MEGACells.makeId("inscriber/" + output.id().getPath()));
    }

    private void cell(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cellDefinition) {
        var cell = (MEGAStorageCell) cellDefinition.asItem();

        var component = cell.getTier().componentSupplier().get();
        var housing = cell.getType().housing();
        var housingMaterial = cell.getType().housingMaterial();

        var componentPath = Registry.ITEM.getKey(component).getPath();
        var cellPath = cellDefinition.id().getPath();

        ShapedRecipeBuilder.shaped(cell)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + componentPath, has(component))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath));
        ShapelessRecipeBuilder.shapeless(cell)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath + "_with_housing"));
    }

    private void portable(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cellDefinition) {
        var cell = (MEGAPortableCell) cellDefinition.asItem();
        var housing = cell.getType().housing();
        ShapelessRecipeBuilder.shapeless(cell)
                .requires(AEBlocks.CHEST)
                .requires(cell.getTier().componentSupplier().get())
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("cells/portable/" + cellDefinition.id().getPath()));
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
                .save(consumer, MEGACells.makeId("cells/" + housing.id().getPath()));
    }

    private void craftingBlock(Consumer<FinishedRecipe> consumer, BlockDefinition<?> unit, ItemLike part) {
        ShapelessRecipeBuilder.shapeless(unit)
                .requires(MEGABlocks.MEGA_CRAFTING_UNIT)
                .requires(part)
                .unlockedBy("has_mega_crafting_unit", has(MEGABlocks.MEGA_CRAFTING_UNIT))
                .save(consumer, MEGACells.makeId("crafting/" + unit.id().getPath()));
    }

    private InventoryChangeTrigger.TriggerInstance has(ItemLike item) {
        return trigger(ItemPredicate.Builder.item().of(item).build());
    }

    private InventoryChangeTrigger.TriggerInstance trigger(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY,
                MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }
}

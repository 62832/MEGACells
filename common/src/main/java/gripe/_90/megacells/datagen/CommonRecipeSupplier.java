package gripe._90.megacells.datagen;

import java.util.function.Consumer;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.util.Utils;

public class CommonRecipeSupplier {
    public static void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // spotless:off
        component(consumer, MEGAItems.TIER_1M, StorageTier.SIZE_256K, AEItems.SKY_DUST.asItem());
        component(consumer, MEGAItems.TIER_4M, MEGAItems.TIER_1M, AEItems.ENDER_DUST.asItem());
        component(consumer, MEGAItems.TIER_16M, MEGAItems.TIER_4M, AEItems.ENDER_DUST.asItem());
        component(consumer, MEGAItems.TIER_64M, MEGAItems.TIER_16M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGAItems.TIER_256M, MEGAItems.TIER_64M, AEItems.MATTER_BALL.asItem());

        housing(consumer, MEGAItems.MEGA_ITEM_CELL_HOUSING, ConventionTags.IRON_INGOT);
        housing(consumer, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);

        cell(consumer, MEGAItems.ITEM_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(consumer, MEGAItems.ITEM_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(consumer, MEGAItems.ITEM_CELL_16M, MEGAItems.CELL_COMPONENT_16M, MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(consumer, MEGAItems.ITEM_CELL_64M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(consumer, MEGAItems.ITEM_CELL_256M, MEGAItems.CELL_COMPONENT_256M, MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(consumer, MEGAItems.FLUID_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(consumer, MEGAItems.FLUID_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(consumer, MEGAItems.FLUID_CELL_16M, MEGAItems.CELL_COMPONENT_16M, MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(consumer, MEGAItems.FLUID_CELL_64M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(consumer, MEGAItems.FLUID_CELL_256M, MEGAItems.CELL_COMPONENT_256M, MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);

        portable(consumer, MEGAItems.PORTABLE_ITEM_CELL_1M, MEGAItems.CELL_COMPONENT_1M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_ITEM_CELL_4M, MEGAItems.CELL_COMPONENT_4M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_ITEM_CELL_16M, MEGAItems.CELL_COMPONENT_16M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_ITEM_CELL_64M, MEGAItems.CELL_COMPONENT_64M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_ITEM_CELL_256M, MEGAItems.CELL_COMPONENT_256M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_FLUID_CELL_1M, MEGAItems.CELL_COMPONENT_1M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_FLUID_CELL_4M, MEGAItems.CELL_COMPONENT_4M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_FLUID_CELL_16M, MEGAItems.CELL_COMPONENT_16M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_FLUID_CELL_64M, MEGAItems.CELL_COMPONENT_64M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(consumer, MEGAItems.PORTABLE_FLUID_CELL_256M, MEGAItems.CELL_COMPONENT_256M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);

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
                .save(consumer, Utils.makeId("cells/standard/bulk_item_cell"));

        ShapedRecipeBuilder.shaped(MEGABlocks.MEGA_ENERGY_CELL)
                .pattern("aaa")
                .pattern("aba")
                .pattern("aaa")
                .define('a', AEBlocks.DENSE_ENERGY_CELL)
                .define('b', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .unlockedBy("has_engineering_processor", has(AEItems.ENGINEERING_PROCESSOR))
                .save(consumer, Utils.makeId("crafting/mega_energy_cell"));

        ShapedRecipeBuilder.shaped(MEGABlocks.MEGA_CRAFTING_UNIT)
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', ConventionTags.IRON_INGOT)
                .define('b', AEItems.LOGIC_PROCESSOR)
                .define('c', AEParts.SMART_CABLE.item(AEColor.TRANSPARENT))
                .define('d', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("has_logic_processor", has(AEItems.LOGIC_PROCESSOR))
                .save(consumer, Utils.makeId("crafting/mega_crafting_unit"));
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
                .save(consumer, Utils.makeId("crafting/greater_energy_card"));
        ShapelessRecipeBuilder.shapeless(MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ENERGY_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(consumer, Utils.makeId("crafting/greater_energy_card_upgraded"));

        ShapelessRecipeBuilder.shapeless(MEGAItems.COMPRESSION_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(AEItems.MATTER_BALL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_matter_ball", has(AEItems.MATTER_BALL))
                .save(consumer, Utils.makeId("crafting/compression_card"));

        ShapelessRecipeBuilder.shapeless(MEGAParts.MEGA_PATTERN_PROVIDER)
                .requires(MEGABlocks.MEGA_PATTERN_PROVIDER)
                .unlockedBy("has_mega_pattern_provider", has(MEGABlocks.MEGA_PATTERN_PROVIDER))
                .save(consumer, Utils.makeId("network/mega_pattern_provider_part"));
        ShapelessRecipeBuilder.shapeless(MEGABlocks.MEGA_PATTERN_PROVIDER)
                .requires(MEGAParts.MEGA_PATTERN_PROVIDER)
                .unlockedBy("has_cable_mega_pattern_provider", has(MEGAParts.MEGA_PATTERN_PROVIDER))
                .save(consumer, Utils.makeId("network/mega_pattern_provider_block"));

        manaCells(consumer, AppBotItems.MANA_CELL_1M, AppBotItems.PORTABLE_MANA_CELL_1M, MEGAItems.TIER_1M);
        manaCells(consumer, AppBotItems.MANA_CELL_4M, AppBotItems.PORTABLE_MANA_CELL_4M, MEGAItems.TIER_4M);
        manaCells(consumer, AppBotItems.MANA_CELL_16M, AppBotItems.PORTABLE_MANA_CELL_16M, MEGAItems.TIER_16M);
        manaCells(consumer, AppBotItems.MANA_CELL_64M, AppBotItems.PORTABLE_MANA_CELL_64M, MEGAItems.TIER_64M);
        manaCells(consumer, AppBotItems.MANA_CELL_256M, AppBotItems.PORTABLE_MANA_CELL_256M, MEGAItems.TIER_256M);
    }

    private static void component(Consumer<FinishedRecipe> consumer, StorageTier tier, StorageTier preceding,
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
                        Utils.makeId("cells/" + Registry.ITEM.getKey(tier.componentSupplier().get()).getPath()));
    }

    private static void specialisedComponent(Consumer<FinishedRecipe> consumer, ItemLike top, ItemLike bottom,
            ItemDefinition<?> output) {
        InscriberRecipeBuilder.inscribe(AEItems.SINGULARITY, output, 1)
                .setMode(InscriberProcessType.PRESS)
                .setTop(Ingredient.of(top)).setBottom(Ingredient.of(bottom))
                .save(consumer, Utils.makeId("inscriber/" + output.id().getPath()));
    }

    private static void cell(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> component,
            ItemDefinition<?> housing, TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(cell)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .save(consumer, Utils.makeId("cells/standard/" + cell.id().getPath()));
        ShapelessRecipeBuilder.shapeless(cell)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .save(consumer, Utils.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));
    }

    private static void portable(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> component,
            ItemDefinition<?> housing) {
        ShapelessRecipeBuilder.shapeless(cell)
                .requires(AEBlocks.CHEST)
                .requires(component)
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, Utils.makeId("cells/portable/" + cell.id().getPath()));
    }

    private static void housing(Consumer<FinishedRecipe> consumer, ItemDefinition<?> housing,
            TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(housing)
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', housingMaterial)
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, Utils.makeId("cells/" + housing.id().getPath()));
    }

    private static void craftingBlock(Consumer<FinishedRecipe> consumer, BlockDefinition<?> unit, ItemLike part) {
        ShapelessRecipeBuilder.shapeless(unit)
                .requires(MEGABlocks.MEGA_CRAFTING_UNIT)
                .requires(part)
                .unlockedBy("has_mega_crafting_unit", has(MEGABlocks.MEGA_CRAFTING_UNIT))
                .save(consumer, Utils.makeId("crafting/" + unit.id().getPath()));
    }

    private static void manaCells(Consumer<FinishedRecipe> consumer, ItemDefinition<?> cell, ItemDefinition<?> portable,
            StorageTier tier) {
        var component = tier.componentSupplier().get();
        var componentPath = Registry.ITEM.getKey(component).getPath();
        ShapelessRecipeBuilder.shapeless(cell)
                .requires(AppBotItems.MEGA_MANA_CELL_HOUSING)
                .requires(tier.componentSupplier().get())
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_mega_mana_cell_housing", has(AppBotItems.MEGA_MANA_CELL_HOUSING))
                .save(consumer, Utils.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));
        ShapelessRecipeBuilder.shapeless(portable)
                .requires(AEBlocks.CHEST)
                .requires(component)
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(AppBotItems.MEGA_MANA_CELL_HOUSING)
                .unlockedBy("has_mega_mana_cell_housing", has(AppBotItems.MEGA_MANA_CELL_HOUSING))
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, Utils.makeId("cells/portable/" + portable.id().getPath()));
    }

    private static InventoryChangeTrigger.TriggerInstance has(ItemLike item) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY,
                MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,
                new ItemPredicate[] { ItemPredicate.Builder.item().of(item).build() });
    }
}

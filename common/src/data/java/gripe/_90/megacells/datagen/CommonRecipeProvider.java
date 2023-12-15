package gripe._90.megacells.datagen;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.FluidTags;
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
import appeng.recipes.transform.TransformCircumstance;
import appeng.recipes.transform.TransformRecipeBuilder;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATags;
import gripe._90.megacells.integration.appbot.AppBotItems;

public class CommonRecipeProvider extends RecipeProvider {
    private final PackOutput output;

    public CommonRecipeProvider(PackOutput output) {
        super(output);
        this.output = output;
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> writer) {
        component(writer, MEGAItems.TIER_1M, StorageTier.SIZE_256K, AEItems.SKY_DUST.asItem(), null);
        component(writer, MEGAItems.TIER_4M, MEGAItems.TIER_1M, null, ConventionTags.ENDER_PEARL_DUST);
        component(writer, MEGAItems.TIER_16M, MEGAItems.TIER_4M, null, ConventionTags.ENDER_PEARL_DUST);
        component(writer, MEGAItems.TIER_64M, MEGAItems.TIER_16M, AEItems.MATTER_BALL.asItem(), null);
        component(writer, MEGAItems.TIER_256M, MEGAItems.TIER_64M, AEItems.MATTER_BALL.asItem(), null);

        housing(writer, MEGAItems.MEGA_ITEM_CELL_HOUSING, MEGATags.SKY_STEEL_INGOT);
        housing(writer, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);

        cell(
                writer,
                MEGAItems.ITEM_CELL_1M,
                MEGAItems.CELL_COMPONENT_1M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(
                writer,
                MEGAItems.ITEM_CELL_4M,
                MEGAItems.CELL_COMPONENT_4M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(
                writer,
                MEGAItems.ITEM_CELL_16M,
                MEGAItems.CELL_COMPONENT_16M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(
                writer,
                MEGAItems.ITEM_CELL_64M,
                MEGAItems.CELL_COMPONENT_64M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(
                writer,
                MEGAItems.ITEM_CELL_256M,
                MEGAItems.CELL_COMPONENT_256M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING,
                ConventionTags.IRON_INGOT);
        cell(
                writer,
                MEGAItems.FLUID_CELL_1M,
                MEGAItems.CELL_COMPONENT_1M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(
                writer,
                MEGAItems.FLUID_CELL_4M,
                MEGAItems.CELL_COMPONENT_4M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(
                writer,
                MEGAItems.FLUID_CELL_16M,
                MEGAItems.CELL_COMPONENT_16M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(
                writer,
                MEGAItems.FLUID_CELL_64M,
                MEGAItems.CELL_COMPONENT_64M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);
        cell(
                writer,
                MEGAItems.FLUID_CELL_256M,
                MEGAItems.CELL_COMPONENT_256M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING,
                ConventionTags.COPPER_INGOT);

        portable(
                writer, MEGAItems.PORTABLE_ITEM_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(
                writer, MEGAItems.PORTABLE_ITEM_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_ITEM_CELL_16M,
                MEGAItems.CELL_COMPONENT_16M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_ITEM_CELL_64M,
                MEGAItems.CELL_COMPONENT_64M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_ITEM_CELL_256M,
                MEGAItems.CELL_COMPONENT_256M,
                MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_FLUID_CELL_1M,
                MEGAItems.CELL_COMPONENT_1M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_FLUID_CELL_4M,
                MEGAItems.CELL_COMPONENT_4M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_FLUID_CELL_16M,
                MEGAItems.CELL_COMPONENT_16M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_FLUID_CELL_64M,
                MEGAItems.CELL_COMPONENT_64M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(
                writer,
                MEGAItems.PORTABLE_FLUID_CELL_256M,
                MEGAItems.CELL_COMPONENT_256M,
                MEGAItems.MEGA_FLUID_CELL_HOUSING);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MEGABlocks.SKY_STEEL_BLOCK)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', MEGATags.SKY_STEEL_INGOT)
                .unlockedBy("has_sky_steel_ingot", has(MEGATags.SKY_STEEL_INGOT))
                .save(writer, MEGACells.makeId("crafting/sky_steel_block"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.SKY_STEEL_INGOT, 9)
                .requires(MEGABlocks.SKY_STEEL_BLOCK)
                .unlockedBy("has_sky_steel_ingot", has(MEGATags.SKY_STEEL_INGOT))
                .save(writer, MEGACells.makeId("crafting/sky_steel_ingot_from_sky_steel_block"));

        TransformRecipeBuilder.transform(
                writer,
                MEGACells.makeId("transform/sky_steel_ingot"),
                MEGAItems.SKY_STEEL_INGOT,
                2,
                TransformCircumstance.fluid(FluidTags.LAVA),
                Ingredient.of(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED),
                Ingredient.of(ConventionTags.IRON_INGOT),
                Ingredient.of(AEBlocks.SKY_STONE_BLOCK));

        InscriberRecipeBuilder.inscribe(AEItems.SINGULARITY, MEGAItems.ACCUMULATION_PROCESSOR_PRESS, 1)
                .setTop(Ingredient.of(AEItems.CALCULATION_PROCESSOR_PRESS))
                .setBottom(Ingredient.of(AEItems.ENGINEERING_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.PRESS)
                .save(writer, MEGACells.makeId("inscriber/accumulation_processor_press"));
        InscriberRecipeBuilder.inscribe(Items.IRON_BLOCK, MEGAItems.ACCUMULATION_PROCESSOR_PRESS, 1)
                .setTop(Ingredient.of(MEGAItems.ACCUMULATION_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(writer, MEGACells.makeId("inscriber/accumulation_processor_press_extra"));

        InscriberRecipeBuilder.inscribe(MEGATags.SKY_STEEL_INGOT, MEGAItems.ACCUMULATION_PROCESSOR_PRINT, 1)
                .setTop(Ingredient.of(MEGAItems.ACCUMULATION_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(writer, MEGACells.makeId("inscriber/accumulation_processor_print"));
        InscriberRecipeBuilder.inscribe(ConventionTags.FLUIX_DUST, MEGAItems.ACCUMULATION_PROCESSOR, 1)
                .setTop(Ingredient.of(MEGAItems.ACCUMULATION_PROCESSOR_PRINT))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(writer, MEGACells.makeId("inscriber/accumulation_processor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.BULK_CELL_COMPONENT)
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', AEItems.SKY_DUST)
                .define('b', AEItems.SPATIAL_2_CELL_COMPONENT)
                .define('c', MEGAItems.ACCUMULATION_PROCESSOR)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('e', MEGAItems.CELL_COMPONENT_1M)
                .unlockedBy("has_cell_component_1m", has(MEGAItems.CELL_COMPONENT_1M))
                .unlockedBy("has_2_cubed_spatial_cell_component", has(AEItems.SPATIAL_2_CELL_COMPONENT))
                .save(writer, MEGACells.makeId("crafting/bulk_cell_component"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.BULK_ITEM_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', MEGAItems.BULK_CELL_COMPONENT)
                .define('d', Items.NETHERITE_INGOT)
                .unlockedBy("has_bulk_cell_component", has(MEGAItems.BULK_CELL_COMPONENT))
                .save(writer, MEGACells.makeId("cells/standard/bulk_item_cell"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGABlocks.MEGA_ENERGY_CELL)
                .pattern("aaa")
                .pattern("aba")
                .pattern("aaa")
                .define('a', AEBlocks.DENSE_ENERGY_CELL)
                .define('b', MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .save(writer, MEGACells.makeId("crafting/mega_energy_cell"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGABlocks.MEGA_CRAFTING_UNIT)
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', AEBlocks.CRAFTING_UNIT)
                .define('b', AEItems.LOGIC_PROCESSOR)
                .define('c', AEParts.SMART_CABLE.item(AEColor.TRANSPARENT))
                .define('d', MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_logic_processor", has(AEItems.LOGIC_PROCESSOR))
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .save(writer, MEGACells.makeId("crafting/mega_crafting_unit"));
        craftingBlock(writer, MEGABlocks.CRAFTING_ACCELERATOR, AEItems.ENGINEERING_PROCESSOR);
        craftingBlock(writer, MEGABlocks.CRAFTING_STORAGE_1M, MEGAItems.CELL_COMPONENT_1M);
        craftingBlock(writer, MEGABlocks.CRAFTING_STORAGE_4M, MEGAItems.CELL_COMPONENT_4M);
        craftingBlock(writer, MEGABlocks.CRAFTING_STORAGE_16M, MEGAItems.CELL_COMPONENT_16M);
        craftingBlock(writer, MEGABlocks.CRAFTING_STORAGE_64M, MEGAItems.CELL_COMPONENT_64M);
        craftingBlock(writer, MEGABlocks.CRAFTING_STORAGE_256M, MEGAItems.CELL_COMPONENT_256M);
        craftingBlock(writer, MEGABlocks.CRAFTING_MONITOR, AEParts.STORAGE_MONITOR);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(writer, MEGACells.makeId("crafting/greater_energy_card"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ENERGY_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(writer, MEGACells.makeId("crafting/greater_energy_card_upgraded"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.COMPRESSION_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(AEItems.MATTER_BALL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_matter_ball", has(AEItems.MATTER_BALL))
                .save(writer, MEGACells.makeId("crafting/compression_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.DECOMPRESSION_MODULE)
                .pattern("IAI")
                .pattern("C#E")
                .pattern("ILI")
                .define('I', ConventionTags.IRON_INGOT)
                .define('A', MEGAItems.ACCUMULATION_PROCESSOR)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('#', MEGAItems.COMPRESSION_CARD)
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .unlockedBy("has_compression_card", has(MEGAItems.COMPRESSION_CARD))
                .save(writer, MEGACells.makeId("crafting/decompression_module"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_INTERFACE)
                .requires(AEBlocks.INTERFACE)
                .requires(MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .unlockedBy("has_interface", has(ConventionTags.INTERFACE))
                .save(writer, MEGACells.makeId("network/mega_interface"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_PATTERN_PROVIDER)
                .requires(AEBlocks.PATTERN_PROVIDER)
                .requires(MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .unlockedBy("has_pattern_provider", has(ConventionTags.PATTERN_PROVIDER))
                .save(writer, MEGACells.makeId("network/mega_pattern_provider"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.MEGA_INTERFACE)
                .requires(MEGABlocks.MEGA_INTERFACE)
                .unlockedBy("has_mega_interface", has(MEGABlocks.MEGA_INTERFACE))
                .save(writer, MEGACells.makeId("network/mega_interface_part"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_INTERFACE)
                .requires(MEGAItems.MEGA_INTERFACE)
                .unlockedBy("has_cable_mega_interface", has(MEGAItems.MEGA_INTERFACE))
                .save(writer, MEGACells.makeId("network/mega_interface_block"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.MEGA_PATTERN_PROVIDER)
                .requires(MEGABlocks.MEGA_PATTERN_PROVIDER)
                .unlockedBy("has_mega_pattern_provider", has(MEGABlocks.MEGA_PATTERN_PROVIDER))
                .save(writer, MEGACells.makeId("network/mega_pattern_provider_part"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_PATTERN_PROVIDER)
                .requires(MEGAItems.MEGA_PATTERN_PROVIDER)
                .unlockedBy("has_cable_mega_pattern_provider", has(MEGAItems.MEGA_PATTERN_PROVIDER))
                .save(writer, MEGACells.makeId("network/mega_pattern_provider_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.CELL_DOCK)
                .pattern("ICI")
                .pattern(" # ")
                .define('I', ConventionTags.IRON_INGOT)
                .define('C', ConventionTags.COPPER_INGOT)
                .define('#', ConventionTags.GLASS_CABLE)
                .unlockedBy("has_glass_cable", has(ConventionTags.GLASS_CABLE))
                .save(writer, MEGACells.makeId("network/cell_dock"));

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            manaCells(writer, AppBotItems.MANA_CELL_1M, AppBotItems.PORTABLE_MANA_CELL_1M, MEGAItems.TIER_1M);
            manaCells(writer, AppBotItems.MANA_CELL_4M, AppBotItems.PORTABLE_MANA_CELL_4M, MEGAItems.TIER_4M);
            manaCells(writer, AppBotItems.MANA_CELL_16M, AppBotItems.PORTABLE_MANA_CELL_16M, MEGAItems.TIER_16M);
            manaCells(writer, AppBotItems.MANA_CELL_64M, AppBotItems.PORTABLE_MANA_CELL_64M, MEGAItems.TIER_64M);
            manaCells(writer, AppBotItems.MANA_CELL_256M, AppBotItems.PORTABLE_MANA_CELL_256M, MEGAItems.TIER_256M);

            new AppBotHousingRecipeProvider(output).buildRecipes(writer);
        }
    }

    private static void component(
            Consumer<FinishedRecipe> writer,
            StorageTier tier,
            StorageTier preceding,
            ItemLike binderItem,
            TagKey<Item> binderTag) {
        var precedingComponent = preceding.componentSupplier().get();
        var recipe = ShapedRecipeBuilder.shaped(
                        RecipeCategory.MISC, tier.componentSupplier().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca");

        if (binderItem != null) {
            recipe.define('a', binderItem);
        } else if (binderTag != null) {
            recipe.define('a', binderTag);
        }

        recipe.define('b', MEGAItems.ACCUMULATION_PROCESSOR)
                .define('c', precedingComponent)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .unlockedBy(
                        "has_"
                                + BuiltInRegistries.ITEM
                                        .getKey(precedingComponent)
                                        .getPath(),
                        has(precedingComponent))
                .save(
                        writer,
                        MEGACells.makeId("cells/"
                                + BuiltInRegistries.ITEM
                                        .getKey(tier.componentSupplier().get())
                                        .getPath()));
    }

    private static void cell(
            Consumer<FinishedRecipe> writer,
            ItemDefinition<?> cell,
            ItemDefinition<?> component,
            ItemDefinition<?> housing,
            TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cell)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .save(writer, MEGACells.makeId("cells/standard/" + cell.id().getPath()));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .save(writer, MEGACells.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));
    }

    private static void portable(
            Consumer<FinishedRecipe> writer,
            ItemDefinition<?> cell,
            ItemDefinition<?> component,
            ItemDefinition<?> housing) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(AEBlocks.CHEST)
                .requires(component)
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(writer, MEGACells.makeId("cells/portable/" + cell.id().getPath()));
    }

    private static void housing(
            Consumer<FinishedRecipe> writer, ItemDefinition<?> housing, TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, housing)
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', housingMaterial)
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(writer, MEGACells.makeId("cells/" + housing.id().getPath()));
    }

    private static void craftingBlock(Consumer<FinishedRecipe> writer, BlockDefinition<?> unit, ItemLike part) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unit)
                .requires(MEGABlocks.MEGA_CRAFTING_UNIT)
                .requires(part)
                .unlockedBy("has_mega_crafting_unit", has(MEGABlocks.MEGA_CRAFTING_UNIT))
                .save(writer, MEGACells.makeId("crafting/" + unit.id().getPath()));
    }

    private static void manaCells(
            Consumer<FinishedRecipe> writer, ItemDefinition<?> cell, ItemDefinition<?> portable, StorageTier tier) {
        var component = tier.componentSupplier().get();
        var componentPath = BuiltInRegistries.ITEM.getKey(component).getPath();

        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                        .requires(AppBotItems.MEGA_MANA_CELL_HOUSING)
                        .requires(tier.componentSupplier().get())
                        .unlockedBy("has_" + componentPath, has(component))
                        .unlockedBy("has_mega_mana_cell_housing", has(AppBotItems.MEGA_MANA_CELL_HOUSING)),
                Addons.APPBOT,
                MEGACells.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));

        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, portable)
                        .requires(AEBlocks.CHEST)
                        .requires(component)
                        .requires(AEBlocks.DENSE_ENERGY_CELL)
                        .requires(AppBotItems.MEGA_MANA_CELL_HOUSING)
                        .unlockedBy("has_mega_mana_cell_housing", has(AppBotItems.MEGA_MANA_CELL_HOUSING))
                        .unlockedBy("has_" + componentPath, has(component))
                        .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL)),
                Addons.APPBOT,
                MEGACells.makeId("cells/portable/" + portable.id().getPath()));
    }

    @NotNull
    @Override
    public String getName() {
        return "Common Recipes";
    }
}

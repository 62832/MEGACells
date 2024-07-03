package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
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
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATags;

public class MEGARecipeProvider extends RecipeProvider {
    private static final TagKey<Item> OSMIUM = ItemTags.create(ResourceLocation.parse("forge:ingots/osmium"));

    public MEGARecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        // spotless:off
        component(output, MEGAItems.TIER_1M, StorageTier.SIZE_256K, AEItems.SKY_DUST.asItem(), null);
        component(output, MEGAItems.TIER_4M, MEGAItems.TIER_1M, null, ConventionTags.ENDER_PEARL_DUST);
        component(output, MEGAItems.TIER_16M, MEGAItems.TIER_4M, null, ConventionTags.ENDER_PEARL_DUST);
        component(output, MEGAItems.TIER_64M, MEGAItems.TIER_16M, AEItems.MATTER_BALL.asItem(), null);
        component(output, MEGAItems.TIER_256M, MEGAItems.TIER_64M, AEItems.MATTER_BALL.asItem(), null);

        housing(output, MEGAItems.MEGA_ITEM_CELL_HOUSING, MEGATags.SKY_STEEL_INGOT);
        housing(output, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);

        cell(output, MEGAItems.ITEM_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_ITEM_CELL_HOUSING, ConventionTags.IRON_INGOT);
        cell(output, MEGAItems.ITEM_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_ITEM_CELL_HOUSING, ConventionTags.IRON_INGOT);
        cell(output, MEGAItems.ITEM_CELL_16M, MEGAItems.CELL_COMPONENT_16M, MEGAItems.MEGA_ITEM_CELL_HOUSING, ConventionTags.IRON_INGOT);
        cell(output, MEGAItems.ITEM_CELL_64M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.MEGA_ITEM_CELL_HOUSING, ConventionTags.IRON_INGOT);
        cell(output, MEGAItems.ITEM_CELL_256M, MEGAItems.CELL_COMPONENT_256M, MEGAItems.MEGA_ITEM_CELL_HOUSING, ConventionTags.IRON_INGOT);

        cell(output, MEGAItems.FLUID_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);
        cell(output, MEGAItems.FLUID_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);
        cell(output, MEGAItems.FLUID_CELL_16M, MEGAItems.CELL_COMPONENT_16M, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);
        cell(output, MEGAItems.FLUID_CELL_64M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);
        cell(output, MEGAItems.FLUID_CELL_256M, MEGAItems.CELL_COMPONENT_256M, MEGAItems.MEGA_FLUID_CELL_HOUSING, ConventionTags.COPPER_INGOT);

        portable(output, MEGAItems.PORTABLE_ITEM_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_ITEM_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_ITEM_CELL_16M, MEGAItems.CELL_COMPONENT_16M, MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_ITEM_CELL_64M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.MEGA_ITEM_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_ITEM_CELL_256M, MEGAItems.CELL_COMPONENT_256M, MEGAItems.MEGA_ITEM_CELL_HOUSING);

        portable(output, MEGAItems.PORTABLE_FLUID_CELL_1M, MEGAItems.CELL_COMPONENT_1M, MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_FLUID_CELL_4M, MEGAItems.CELL_COMPONENT_4M, MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_FLUID_CELL_16M, MEGAItems.CELL_COMPONENT_16M, MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_FLUID_CELL_64M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.MEGA_FLUID_CELL_HOUSING);
        portable(output, MEGAItems.PORTABLE_FLUID_CELL_256M, MEGAItems.CELL_COMPONENT_256M, MEGAItems.MEGA_FLUID_CELL_HOUSING);
        // spotless:on

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MEGABlocks.SKY_STEEL_BLOCK)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', MEGATags.SKY_STEEL_INGOT)
                .unlockedBy("has_sky_steel_ingot", has(MEGATags.SKY_STEEL_INGOT))
                .save(output, MEGACells.makeId("crafting/sky_steel_block"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.SKY_STEEL_INGOT, 9)
                .requires(MEGABlocks.SKY_STEEL_BLOCK)
                .unlockedBy("has_sky_steel_ingot", has(MEGATags.SKY_STEEL_INGOT))
                .save(output, MEGACells.makeId("crafting/sky_steel_ingot_from_sky_steel_block"));

        TransformRecipeBuilder.transform(
                output,
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
                .save(output, MEGACells.makeId("inscriber/accumulation_processor_press"));
        InscriberRecipeBuilder.inscribe(Items.IRON_BLOCK, MEGAItems.ACCUMULATION_PROCESSOR_PRESS, 1)
                .setTop(Ingredient.of(MEGAItems.ACCUMULATION_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(output, MEGACells.makeId("inscriber/accumulation_processor_press_extra"));

        InscriberRecipeBuilder.inscribe(MEGATags.SKY_STEEL_INGOT, MEGAItems.ACCUMULATION_PROCESSOR_PRINT, 1)
                .setTop(Ingredient.of(MEGAItems.ACCUMULATION_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(output, MEGACells.makeId("inscriber/accumulation_processor_print"));
        InscriberRecipeBuilder.inscribe(ConventionTags.FLUIX_DUST, MEGAItems.ACCUMULATION_PROCESSOR, 1)
                .setTop(Ingredient.of(MEGAItems.ACCUMULATION_PROCESSOR_PRINT))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(output, MEGACells.makeId("inscriber/accumulation_processor"));

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
                .save(output, MEGACells.makeId("crafting/bulk_cell_component"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.BULK_ITEM_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', MEGAItems.BULK_CELL_COMPONENT)
                .define('d', Items.NETHERITE_INGOT)
                .unlockedBy("has_bulk_cell_component", has(MEGAItems.BULK_CELL_COMPONENT))
                .save(output, MEGACells.makeId("cells/standard/bulk_item_cell"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGABlocks.MEGA_ENERGY_CELL)
                .pattern("aaa")
                .pattern("aba")
                .pattern("aaa")
                .define('a', AEBlocks.DENSE_ENERGY_CELL)
                .define('b', MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .save(output, MEGACells.makeId("crafting/mega_energy_cell"));

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
                .save(output, MEGACells.makeId("crafting/mega_crafting_unit"));
        craftingBlock(output, MEGABlocks.CRAFTING_ACCELERATOR, AEItems.ENGINEERING_PROCESSOR);
        craftingBlock(output, MEGABlocks.CRAFTING_STORAGE_1M, MEGAItems.CELL_COMPONENT_1M);
        craftingBlock(output, MEGABlocks.CRAFTING_STORAGE_4M, MEGAItems.CELL_COMPONENT_4M);
        craftingBlock(output, MEGABlocks.CRAFTING_STORAGE_16M, MEGAItems.CELL_COMPONENT_16M);
        craftingBlock(output, MEGABlocks.CRAFTING_STORAGE_64M, MEGAItems.CELL_COMPONENT_64M);
        craftingBlock(output, MEGABlocks.CRAFTING_STORAGE_256M, MEGAItems.CELL_COMPONENT_256M);
        craftingBlock(output, MEGABlocks.CRAFTING_MONITOR, AEParts.STORAGE_MONITOR);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(output, MEGACells.makeId("crafting/greater_energy_card"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.GREATER_ENERGY_CARD)
                .requires(AEItems.ENERGY_CARD)
                .requires(MEGABlocks.MEGA_ENERGY_CELL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_mega_energy_cell", has(MEGABlocks.MEGA_ENERGY_CELL))
                .save(output, MEGACells.makeId("crafting/greater_energy_card_upgraded"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.COMPRESSION_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(AEItems.MATTER_BALL)
                .unlockedBy("has_advanced_card", has(AEItems.ADVANCED_CARD))
                .unlockedBy("has_matter_ball", has(AEItems.MATTER_BALL))
                .save(output, MEGACells.makeId("crafting/compression_card"));
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
                .save(output, MEGACells.makeId("crafting/decompression_module"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_INTERFACE)
                .requires(AEBlocks.INTERFACE)
                .requires(MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .unlockedBy("has_interface", has(ConventionTags.INTERFACE))
                .save(output, MEGACells.makeId("network/mega_interface"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_PATTERN_PROVIDER)
                .requires(AEBlocks.PATTERN_PROVIDER)
                .requires(MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .unlockedBy("has_pattern_provider", has(ConventionTags.PATTERN_PROVIDER))
                .save(output, MEGACells.makeId("network/mega_pattern_provider"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.MEGA_INTERFACE)
                .requires(MEGABlocks.MEGA_INTERFACE)
                .unlockedBy("has_mega_interface", has(MEGABlocks.MEGA_INTERFACE))
                .save(output, MEGACells.makeId("network/mega_interface_part"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_INTERFACE)
                .requires(MEGAItems.MEGA_INTERFACE)
                .unlockedBy("has_cable_mega_interface", has(MEGAItems.MEGA_INTERFACE))
                .save(output, MEGACells.makeId("network/mega_interface_block"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGAItems.MEGA_PATTERN_PROVIDER)
                .requires(MEGABlocks.MEGA_PATTERN_PROVIDER)
                .unlockedBy("has_mega_pattern_provider", has(MEGABlocks.MEGA_PATTERN_PROVIDER))
                .save(output, MEGACells.makeId("network/mega_pattern_provider_part"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_PATTERN_PROVIDER)
                .requires(MEGAItems.MEGA_PATTERN_PROVIDER)
                .unlockedBy("has_cable_mega_pattern_provider", has(MEGAItems.MEGA_PATTERN_PROVIDER))
                .save(output, MEGACells.makeId("network/mega_pattern_provider_block"));

        /*
        if (Addons.APPMEK.isLoaded()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
                    .pattern("aba")
                    .pattern("b b")
                    .pattern("ddd")
                    .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                    .define('b', AEItems.SKY_DUST)
                    .define('d', OSMIUM)
                    .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                    .save(
                            Addons.APPMEK.conditionalRecipe(output),
                            MEGACells.makeId("cells/mega_chemical_cell_housing"));

            cell(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.CHEMICAL_CELL_1M,
                    MEGAItems.CELL_COMPONENT_1M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING,
                    OSMIUM);
            cell(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.CHEMICAL_CELL_4M,
                    MEGAItems.CELL_COMPONENT_4M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING,
                    OSMIUM);
            cell(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.CHEMICAL_CELL_16M,
                    MEGAItems.CELL_COMPONENT_16M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING,
                    OSMIUM);
            cell(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.CHEMICAL_CELL_64M,
                    MEGAItems.CELL_COMPONENT_64M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING,
                    OSMIUM);
            cell(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.CHEMICAL_CELL_256M,
                    MEGAItems.CELL_COMPONENT_256M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING,
                    OSMIUM);

            portable(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.PORTABLE_CHEMICAL_CELL_1M,
                    MEGAItems.CELL_COMPONENT_1M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);
            portable(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.PORTABLE_CHEMICAL_CELL_4M,
                    MEGAItems.CELL_COMPONENT_4M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);
            portable(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.PORTABLE_CHEMICAL_CELL_16M,
                    MEGAItems.CELL_COMPONENT_16M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);
            portable(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.PORTABLE_CHEMICAL_CELL_64M,
                    MEGAItems.CELL_COMPONENT_64M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);
            portable(
                    Addons.APPMEK.conditionalRecipe(output),
                    AppMekItems.PORTABLE_CHEMICAL_CELL_256M,
                    MEGAItems.CELL_COMPONENT_256M,
                    AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);

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
                    .unlockedBy("has_waste_barrel", has(MekanismBlocks.RADIOACTIVE_WASTE_BARREL))
                    .save(
                            Addons.APPMEK.conditionalRecipe(output),
                            MEGACells.makeId("crafting/radioactive_cell_component"));

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
                    .save(
                            Addons.APPMEK.conditionalRecipe(output),
                            MEGACells.makeId("cells/standard/radioactive_chemical_cell"));
        }
         */
    }

    private static void component(
            RecipeOutput output, StorageTier tier, StorageTier preceding, ItemLike binderItem, TagKey<Item> binderTag) {
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
                        output,
                        MEGACells.makeId("cells/"
                                + BuiltInRegistries.ITEM
                                        .getKey(tier.componentSupplier().get())
                                        .getPath()));
    }

    private static void housing(RecipeOutput output, ItemDefinition<?> housing, TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, housing)
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', housingMaterial)
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(output, MEGACells.makeId("cells/" + housing.id().getPath()));
    }

    private static void cell(
            RecipeOutput output, ItemDefinition<?> cell, ItemDefinition<?> component, ItemDefinition<?> housing) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .save(output, MEGACells.makeId("cells/standard/" + cell.id().getPath() + "_with_housing"));
    }

    private static void cell(
            RecipeOutput output,
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
                .save(output, MEGACells.makeId("cells/standard/" + cell.id().getPath()));
        cell(output, cell, component, housing);
    }

    private static void portable(
            RecipeOutput output, ItemDefinition<?> cell, ItemDefinition<?> component, ItemDefinition<?> housing) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(AEBlocks.CHEST)
                .requires(component)
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(output, MEGACells.makeId("cells/portable/" + cell.id().getPath()));
    }

    private static void craftingBlock(RecipeOutput output, BlockDefinition<?> unit, ItemLike part) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unit)
                .requires(MEGABlocks.MEGA_CRAFTING_UNIT)
                .requires(part)
                .unlockedBy("has_mega_crafting_unit", has(MEGABlocks.MEGA_CRAFTING_UNIT))
                .save(output, MEGACells.makeId("crafting/" + unit.id().getPath()));
    }
}

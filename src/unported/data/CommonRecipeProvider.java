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
        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            manaCells(writer, AppBotItems.MANA_CELL_1M, AppBotItems.PORTABLE_MANA_CELL_1M, MEGAItems.TIER_1M);
            manaCells(writer, AppBotItems.MANA_CELL_4M, AppBotItems.PORTABLE_MANA_CELL_4M, MEGAItems.TIER_4M);
            manaCells(writer, AppBotItems.MANA_CELL_16M, AppBotItems.PORTABLE_MANA_CELL_16M, MEGAItems.TIER_16M);
            manaCells(writer, AppBotItems.MANA_CELL_64M, AppBotItems.PORTABLE_MANA_CELL_64M, MEGAItems.TIER_64M);
            manaCells(writer, AppBotItems.MANA_CELL_256M, AppBotItems.PORTABLE_MANA_CELL_256M, MEGAItems.TIER_256M);

            new AppBotHousingRecipeProvider(output).buildRecipes(writer);
        }
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
}

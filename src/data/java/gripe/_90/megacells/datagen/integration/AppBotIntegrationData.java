package gripe._90.megacells.datagen.integration;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import appbot.AppliedBotanics;

import gripe._90.megacells.datagen.MEGARecipeProvider;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.appbot.AppBotIntegration;

import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.crafting.RecipeTerraPlate;
import vazkii.botania.common.item.BotaniaItems;

public class AppBotIntegrationData {
    public static final ItemLike MANA_CELL_HOUSING =
            BuiltInRegistries.ITEM.get(AppliedBotanics.id("mana_cell_housing"));

    public static List<ItemLike> getCells() {
        return AppBotIntegration.getCells();
    }

    public static void recipes(RecipeOutput output) {
        MEGARecipeProvider.conditional(output, Addons.APPBOT)
                .accept(
                        MEGAItems.MEGA_MANA_CELL_HOUSING.id(),
                        new RecipeTerraPlate(
                                MEGAItems.MEGA_MANA_CELL_HOUSING.id(),
                                ManaPoolBlockEntity.MAX_MANA * 2,
                                NonNullList.of(
                                        Ingredient.of(
                                                BuiltInRegistries.ITEM.get(AppliedBotanics.id("mana_cell_housing"))),
                                        Ingredient.of(BotaniaItems.manaPearl),
                                        Ingredient.of(BotaniaItems.manaDiamond),
                                        Ingredient.of(AEItems.SKY_DUST),
                                        Ingredient.of(AEBlocks.QUARTZ_VIBRANT_GLASS)),
                                MEGAItems.MEGA_MANA_CELL_HOUSING.stack()),
                        null);
    }
}

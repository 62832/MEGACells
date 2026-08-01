package gripe._90.megacells.datagen.integration;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.appbot.AppBotIntegration;

import vazkii.botania.common.crafting.TerrestrialAgglomerationRecipe;
import vazkii.botania.common.item.BotaniaItems;

public class AppBotIntegrationData {
    public static final ItemLike MANA_CELL_HOUSING =
            BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("appbot", "mana_cell_housing"));

    public static List<ItemLike> getCells() {
        return AppBotIntegration.getCells();
    }

    public static void recipes(RecipeOutput output) {
        output.withConditions(new ModLoadedCondition(Addons.APPBOT.getModId()))
                .accept(
                        ResourceKey.create(Registries.RECIPE, MEGAItems.MEGA_MANA_CELL_HOUSING.id()),
                        new TerrestrialAgglomerationRecipe(
                                10000,
                                MEGAItems.MEGA_MANA_CELL_HOUSING.stack(),
                                Ingredient.of(MANA_CELL_HOUSING),
                                Ingredient.of(BotaniaItems.manaPearl),
                                Ingredient.of(BotaniaItems.manaDiamond),
                                Ingredient.of(AEItems.SKY_DUST),
                                Ingredient.of(AEBlocks.QUARTZ_VIBRANT_GLASS)),
                        null);
    }
}

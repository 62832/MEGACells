package gripe._90.megacells.datagen;

import java.util.function.Consumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import appbot.AppliedBotanics;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.integration.appbot.AppBotItems;

import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.data.recipes.TerrestrialAgglomerationProvider;

public class AppBotHousingRecipeProvider extends TerrestrialAgglomerationProvider {
    public AppBotHousingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(Consumer<net.minecraft.data.recipes.FinishedRecipe> writer) {
        var housing = AppBotItems.MEGA_MANA_CELL_HOUSING;
        var id = MEGACells.makeId("cells/" + housing.id().getPath());
        MEGACells.PLATFORM.addIntegrationRecipe(
                writer,
                new FinishedRecipe(
                        id,
                        ManaPoolBlockEntity.MAX_MANA * 2,
                        housing.stack(),
                        Ingredient.of(BuiltInRegistries.ITEM.get(AppliedBotanics.id("mana_cell_housing"))),
                        Ingredient.of(BotaniaItems.manaPearl),
                        Ingredient.of(BotaniaItems.manaDiamond),
                        Ingredient.of(AEItems.SKY_DUST),
                        Ingredient.of(AEBlocks.QUARTZ_VIBRANT_GLASS)),
                Addons.APPBOT,
                id);
    }
}

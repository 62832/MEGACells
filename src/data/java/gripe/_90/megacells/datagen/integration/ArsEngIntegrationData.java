package gripe._90.megacells.datagen.integration;

import java.util.List;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import appeng.core.definitions.ItemDefinition;

import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;

public class ArsEngIntegrationData {
    public static final ItemLike SOURCE_CELL_HOUSING = ArsEngItems.SOURCE_CELL_HOUSING;

    public static List<ItemDefinition<SourceCellItem>> getCells() {
        return ArsEngItems.getCells();
    }

    public static void recipes(RecipeOutput output) {
        output.withConditions(new ModLoadedCondition(Addons.ARSENG.getModId()))
                .accept(
                        MEGACells.makeId("cells/"
                                + MEGAItems.MEGA_SOURCE_CELL_HOUSING.id().getPath()),
                        new EnchantingApparatusRecipe(
                                Ingredient.of(ArsEngItems.SOURCE_CELL_HOUSING),
                                MEGAItems.MEGA_SOURCE_CELL_HOUSING.stack(),
                                List.of(
                                        Ingredient.of(ItemsRegistry.MANIPULATION_ESSENCE),
                                        Ingredient.of(ItemsRegistry.MANIPULATION_ESSENCE),
                                        Ingredient.of(ItemsRegistry.MANIPULATION_ESSENCE),
                                        Ingredient.of(BlockRegistry.SOURCE_GEM_BLOCK),
                                        Ingredient.of(BlockRegistry.SOURCE_GEM_BLOCK),
                                        Ingredient.of(MEGAItems.SKY_STEEL_INGOT),
                                        Ingredient.of(MEGAItems.SKY_STEEL_INGOT),
                                        Ingredient.of(MEGAItems.SKY_STEEL_INGOT)),
                                2000,
                                false),
                        null);
    }
}

package gripe._90.megacells.datagen.integration;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import me.ramidzkh.mekae2.AMItems;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.registries.GeneratorsBlocks;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;

public class AppMekIntegrationData extends RecipeProvider {
    public static final ItemLike CHEMICAL_CELL_HOUSING = AMItems.CHEMICAL_CELL_HOUSING;

    // slight inheritance abuse just to have access to RecipeProvider::has
    public AppMekIntegrationData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static List<ItemLike> getCells() {
        return List.of(
                AMItems.CHEMICAL_CELL_1K,
                AMItems.CHEMICAL_CELL_4K,
                AMItems.CHEMICAL_CELL_16K,
                AMItems.CHEMICAL_CELL_64K,
                AMItems.CHEMICAL_CELL_256K);
    }

    public static void recipes(RecipeOutput output) {
        var conditional = output.withConditions(new ModLoadedCondition(Addons.APPMEK.getModId()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.RADIOACTIVE_CELL_COMPONENT)
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
                .save(conditional, MEGACells.makeId("crafting/radioactive_cell_component"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MEGAItems.RADIOACTIVE_CHEMICAL_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', GeneratorsBlocks.REACTOR_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', MEGAItems.RADIOACTIVE_CELL_COMPONENT)
                .define('d', MekanismItems.HDPE_SHEET)
                .define('e', MekanismItems.POLONIUM_PELLET)
                .unlockedBy("has_radioactive_cell_component", has(MEGAItems.RADIOACTIVE_CELL_COMPONENT))
                .save(conditional, MEGACells.makeId("cells/standard/radioactive_chemical_cell"));
    }
}

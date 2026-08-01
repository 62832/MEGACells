package gripe._90.megacells.datagen.integration;

import java.util.List;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
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

public class AppMekIntegrationData {
    public static final ItemLike CHEMICAL_CELL_HOUSING = AMItems.CHEMICAL_CELL_HOUSING;

    public static List<ItemLike> getCells() {
        return List.of(
                AMItems.CHEMICAL_CELL_1K,
                AMItems.CHEMICAL_CELL_4K,
                AMItems.CHEMICAL_CELL_16K,
                AMItems.CHEMICAL_CELL_64K,
                AMItems.CHEMICAL_CELL_256K);
    }

    public static void recipes(RecipeOutput output) {
        var items = BuiltInRegistries.ITEM;
        var conditional = output.withConditions(new ModLoadedCondition(Addons.APPMEK.getModId()));
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, MEGAItems.RADIOACTIVE_CELL_COMPONENT)
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', AEItems.SKY_DUST)
                .define('b', MEGAItems.ACCUMULATION_PROCESSOR)
                .define('c', MekanismBlocks.RADIOACTIVE_WASTE_BARREL)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('e', AEItems.CELL_COMPONENT_256K)
                .unlockedBy("has_cell_component_256k", has(items, AEItems.CELL_COMPONENT_256K))
                .unlockedBy("has_waste_barrel", has(items, MekanismBlocks.RADIOACTIVE_WASTE_BARREL))
                .save(
                        conditional,
                        ResourceKey.create(Registries.RECIPE, MEGACells.makeId("crafting/radioactive_cell_component")));
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, MEGAItems.RADIOACTIVE_CHEMICAL_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', GeneratorsBlocks.REACTOR_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', MEGAItems.RADIOACTIVE_CELL_COMPONENT)
                .define('d', MekanismItems.HDPE_SHEET)
                .define('e', MekanismItems.POLONIUM_PELLET)
                .unlockedBy("has_radioactive_cell_component", has(items, MEGAItems.RADIOACTIVE_CELL_COMPONENT))
                .save(
                        conditional,
                        ResourceKey.create(
                                Registries.RECIPE, MEGACells.makeId("cells/standard/radioactive_chemical_cell")));
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(
            net.minecraft.core.HolderGetter<net.minecraft.world.item.Item> items, ItemLike item) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(
                java.util.Optional.empty(),
                InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                List.of(ItemPredicate.Builder.item().of(items, item).build())));
    }
}

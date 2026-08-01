package gripe._90.megacells.datagen.integration;

import java.util.List;
import java.util.Optional;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import gripe._90.appliede.AppliedE;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;

public class AppliedEIntegrationData {
    public static void recipes(RecipeOutput output) {
        var items = BuiltInRegistries.ITEM;
        var conditional = output.withConditions(new ModLoadedCondition(Addons.APPLIEDE.getModId()));
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, MEGABlocks.MEGA_EMC_INTERFACE)
                .requires(AppliedE.EMC_INTERFACE)
                .requires(MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_emc_interface", has(items, AppliedE.EMC_INTERFACE))
                .unlockedBy("has_accumulation_processor", has(items, MEGAItems.ACCUMULATION_PROCESSOR))
                .save(
                        conditional,
                        ResourceKey.create(Registries.RECIPE, MEGACells.makeId("network/mega_emc_interface")));
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> items, ItemLike item) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(
                Optional.empty(),
                InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                List.of(ItemPredicate.Builder.item().of(items, item).build())));
    }
}

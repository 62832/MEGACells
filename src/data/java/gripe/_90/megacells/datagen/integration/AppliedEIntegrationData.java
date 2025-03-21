package gripe._90.megacells.datagen.integration;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import gripe._90.appliede.AppliedE;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;

public class AppliedEIntegrationData extends RecipeProvider {
    public AppliedEIntegrationData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void recipes(RecipeOutput output) {
        var conditional = output.withConditions(new ModLoadedCondition(Addons.APPLIEDE.getModId()));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MEGABlocks.MEGA_EMC_INTERFACE)
                .requires(AppliedE.EMC_INTERFACE)
                .requires(MEGAItems.ACCUMULATION_PROCESSOR)
                .unlockedBy("has_emc_interface", has(AppliedE.EMC_INTERFACE))
                .unlockedBy("has_accumulation_processor", has(MEGAItems.ACCUMULATION_PROCESSOR))
                .save(conditional, MEGACells.makeId("network/mega_emc_interface"));
    }
}

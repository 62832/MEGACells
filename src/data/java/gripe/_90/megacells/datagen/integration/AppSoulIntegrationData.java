package gripe._90.megacells.datagen.integration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.yxiao233.appliedsoul.common.item.SoulCellItem;
import net.yxiao233.appliedsoul.common.registry.SoulItems;

import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.Addons;

public class AppSoulIntegrationData extends RecipeProvider {
    public static final ItemLike SOUL_CELL_HOSING = SoulItems.SOUL_CELL_HOUSING;

    public AppSoulIntegrationData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static List<ItemDefinition<SoulCellItem>> getCells() {
        return SoulItems.getCells();
    }

    public static void recipes(RecipeOutput output) {
        output.withConditions(new ModLoadedCondition(Addons.APPLIEDSOUL.getModId()))
                .accept(
                        MEGAItems.MEGA_SOUL_CELL_HOUSING.id(),
                        new DissolutionChamberRecipe(
                                List.of(
                                        Ingredient.of(Items.ECHO_SHARD),
                                        Ingredient.of(Items.ECHO_SHARD),
                                        Ingredient.of(Items.ECHO_SHARD),
                                        Ingredient.of(ModuleCore.PINK_SLIME_INGOT.get()),
                                        Ingredient.of(ModuleCore.PINK_SLIME_INGOT.get()),
                                        Ingredient.of(IndustrialTags.Items.PLASTIC),
                                        Ingredient.of(SoulItems.SOUL_CELL_HOUSING),
                                        Ingredient.of(IndustrialTags.Items.PLASTIC)),
                                SizedFluidIngredient.of(
                                        ModuleCore.ETHER.getSourceFluid().get(), 1000),
                                100,
                                Optional.of(MEGAItems.MEGA_SOUL_CELL_HOUSING.stack()),
                                Optional.empty()),
                        null);
    }
}

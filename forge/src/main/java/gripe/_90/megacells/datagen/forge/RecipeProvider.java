package gripe._90.megacells.datagen.forge;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;

import gripe._90.megacells.datagen.CommonRecipeSupplier;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        CommonRecipeSupplier.buildRecipes(consumer);
    }
}

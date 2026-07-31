package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

public class RecipeProviderRunner extends RecipeProvider.Runner {
    public RecipeProviderRunner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new MEGARecipeProvider(registries, output);
    }

    @NotNull
    @Override
    public String getName() {
        return "Recipes";
    }
}

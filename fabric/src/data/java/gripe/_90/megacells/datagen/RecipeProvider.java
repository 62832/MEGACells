package gripe._90.megacells.datagen;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;

class RecipeProvider extends FabricRecipeProvider {
    RecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        CommonRecipeProvider.buildRecipes(consumer);
    }
}

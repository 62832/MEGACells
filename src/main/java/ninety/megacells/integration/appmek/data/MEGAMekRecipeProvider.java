package ninety.megacells.integration.appmek.data;

import java.util.function.Consumer;

import ninety.megacells.integration.appmek.MEGAMekIntegration;
import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;

import ninety.megacells.datagen.MEGARecipeProvider;
import ninety.megacells.integration.appmek.ChemicalCellType;

public class MEGAMekRecipeProvider extends MEGARecipeProvider {
    public MEGAMekRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        if (MEGAMekIntegration.isLoaded()) {
            housing(consumer, ChemicalCellType.TYPE);
            for (var storage : ChemicalCellType.TYPE.getCells()) {
                cell(consumer, storage);
            }
            for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
                portable(consumer, portable);
            }
        }
    }
}

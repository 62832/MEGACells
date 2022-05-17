package ninety.megacells.datagen;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.AppMekIntegration;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;
import ninety.megacells.item.util.IMEGACellType;
import ninety.megacells.item.util.MEGACellTier;
import ninety.megacells.item.util.MEGACellType;

public class MEGARecipeProvider extends RecipeProvider {
    public MEGARecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // spotless:off
        component(consumer, MEGACellTier._1M, AEItems.SKY_DUST.asItem());
        component(consumer, MEGACellTier._4M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGACellTier._16M, AEItems.MATTER_BALL.asItem());
        component(consumer, MEGACellTier._64M, AEItems.SINGULARITY.asItem());
        component(consumer, MEGACellTier._256M, AEItems.SINGULARITY.asItem());

        housing(consumer, MEGACellType.ITEM);
        housing(consumer, MEGACellType.FLUID);

        for (var storage : Stream.concat(MEGACellType.ITEM.getCells().stream(), MEGACellType.FLUID.getCells().stream()).toList()) {
            cell(consumer, storage);
        }
        for (var portable : Stream.concat(MEGACellType.ITEM.getPortableCells().stream(), MEGACellType.FLUID.getPortableCells().stream()).toList()) {
            portable(consumer, portable);
        }

        if (AppMekIntegration.isAppMekLoaded()) { // this check doesn't actually do shit lol
            housing(consumer, ChemicalCellType.TYPE);
            for (var chemStorage : ChemicalCellType.TYPE.getCells()) {
                cell(consumer, chemStorage);
            }
            for (var chemPortable : ChemicalCellType.TYPE.getPortableCells()) {
                portable(consumer, chemPortable);
            }
        }
        // spotless:on
    }

    private void component(Consumer<FinishedRecipe> consumer, MEGACellTier tier, Item binder) {
        var preceding = tier == MEGACellTier._1M ? AEItems.CELL_COMPONENT_256K.asItem()
                : MEGACellTier.values()[tier.index - 2].getComponent();

        ShapedRecipeBuilder.shaped(tier.getComponent())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca")
                .define('a', binder)
                .define('b', AEItems.CALCULATION_PROCESSOR)
                .define('c', preceding)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_" + MEGAItems.getItemPath(preceding), has(preceding))
                .save(consumer, MEGACells.makeId("cells/" + MEGAItems.getItemPath(tier.getComponent())));
    }

    private void cell(Consumer<FinishedRecipe> consumer, Item cellItem) {
        var cell = (MEGAStorageCell) cellItem;

        var component = cell.getTier().getComponent();
        var housing = cell.getType().housing();
        var housingMaterial = cell.getType().housingMaterial();

        var componentPath = MEGAItems.getItemPath(component);
        var cellPath = MEGAItems.getItemPath(cellItem);

        ShapedRecipeBuilder.shaped(cellItem)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + componentPath, has(component))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath));
        ShapelessRecipeBuilder.shapeless(cellItem)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_" + MEGAItems.getItemPath(housing), has(housing))
                .save(consumer, MEGACells.makeId("cells/standard/" + cellPath + "_with_housing"));
    }

    private void portable(Consumer<FinishedRecipe> consumer, Item portableCellItem) {
        var portableCell = (MEGAPortableCell) portableCellItem;
        var housing = portableCell.type.housing();
        ShapelessRecipeBuilder.shapeless(portableCell)
                .requires(AEBlocks.CHEST)
                .requires(portableCell.tier.getComponent())
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + MEGAItems.getItemPath(housing), has(housing))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, MEGACells.makeId("cells/portable/" + MEGAItems.getItemPath(portableCell)));
    }

    private void housing(Consumer<FinishedRecipe> consumer, IMEGACellType type) {
        var housing = type.housing();
        ShapedRecipeBuilder.shaped(type.housing())
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', type.housingMaterial())
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, MEGACells.makeId("cells/" + MEGAItems.getItemPath(housing)));
    }
}

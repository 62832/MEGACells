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

import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;
import ninety.megacells.item.util.IMEGACellType;
import ninety.megacells.item.util.MEGACellType;
import ninety.megacells.util.MEGACellsUtil;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

public class MEGARecipeProvider extends RecipeProvider {
    public MEGARecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // spotless:off
        component(consumer, AEItems.CELL_COMPONENT_64K.asItem(), MEGAItems.CELL_COMPONENT_1M.get(), AEItems.SKY_DUST.asItem());
        component(consumer, MEGAItems.CELL_COMPONENT_1M.get(), MEGAItems.CELL_COMPONENT_4M.get(), AEItems.MATTER_BALL.asItem());
        component(consumer, MEGAItems.CELL_COMPONENT_4M.get(), MEGAItems.CELL_COMPONENT_16M.get(), AEItems.MATTER_BALL.asItem());
        component(consumer, MEGAItems.CELL_COMPONENT_16M.get(), MEGAItems.CELL_COMPONENT_64M.get(), AEItems.SINGULARITY.asItem());
        component(consumer, MEGAItems.CELL_COMPONENT_64M.get(), MEGAItems.CELL_COMPONENT_256M.get(), AEItems.SINGULARITY.asItem());

        housing(consumer, MEGACellType.ITEM);
        housing(consumer, MEGACellType.FLUID);

        for (var storage : Stream.concat(MEGACellType.ITEM.getCells().stream(), MEGACellType.FLUID.getCells().stream()).toList()) {
            cell(consumer, storage);
        }
        for (var portable : Stream.concat(MEGACellType.ITEM.getPortableCells().stream(), MEGACellType.FLUID.getPortableCells().stream()).toList()) {
            portable(consumer, portable);
        }
        // spotless:on
    }

    private void component(Consumer<FinishedRecipe> consumer, Item preceding, Item output, Item substrate) {
        ShapedRecipeBuilder.shaped(output)
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca")
                .define('a', substrate)
                .define('b', AEItems.CALCULATION_PROCESSOR)
                .define('c', preceding)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_" + MEGACellsUtil.getItemPath(preceding), has(preceding))
                .save(consumer, MEGACellsUtil.makeId("cells/" + MEGACellsUtil.getItemPath(output)));
    }

    protected void cell(Consumer<FinishedRecipe> consumer, Item cellItem) {
        var cell = (MEGAStorageCell) cellItem;

        var component = cell.getTier().getComponent();
        var housing = cell.getType().housing();
        var housingMaterial = cell.getType().housingMaterial();

        var componentPath = MEGACellsUtil.getItemPath(component);
        var cellPath = MEGACellsUtil.getItemPath(cellItem);

        ShapedRecipeBuilder.shaped(cellItem)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + componentPath, has(component))
                .save(consumer, MEGACellsUtil.makeId("cells/standard" + cellPath));
        ShapelessRecipeBuilder.shapeless(cellItem)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + componentPath, has(component))
                .unlockedBy("has_" + MEGACellsUtil.getItemPath(housing), has(housing))
                .save(consumer, MEGACellsUtil.makeId("cells/standard" + cellPath + "_with_housing"));
    }

    protected void portable(Consumer<FinishedRecipe> consumer, Item portableCellItem) {
        var portableCell = (MEGAPortableCell) portableCellItem;
        var housing = portableCell.type.housing();
        ShapelessRecipeBuilder.shapeless(portableCell)
                .requires(AEBlocks.CHEST)
                .requires(portableCell.tier.getComponent())
                .requires(AEBlocks.DENSE_ENERGY_CELL)
                .requires(housing)
                .unlockedBy("has_" + MEGACellsUtil.getItemPath(housing), has(housing))
                .unlockedBy("has_dense_energy_cell", has(AEBlocks.DENSE_ENERGY_CELL))
                .save(consumer, MEGACellsUtil.makeId("cells/portable/" + MEGACellsUtil.getItemPath(portableCell)));
    }

    protected void housing(Consumer<FinishedRecipe> consumer, IMEGACellType type) {
        var housing = type.housing();
        ShapedRecipeBuilder.shaped(type.housing())
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', type.housingMaterial())
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, MEGACellsUtil.makeId("cells/" + MEGACellsUtil.getItemPath(housing)));
    }
}

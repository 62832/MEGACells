package gripe._90.megacells.datagen.forge;

import java.util.ArrayList;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.definitions.BlockDefinition;
import appeng.init.client.InitItemModelsProperties;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.datagen.CommonModelSupplier;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {

    public BlockStateProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, MEGACells.MODID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        energyCell();

        for (var block : CommonModelSupplier.CRAFTING_UNITS) {
            craftingModel(block.first, block.second);
        }
        craftingMonitorModel();
    }

    private void builtInBlockModel(String name) {
        var model = models().getBuilder("block/" + name);
        var loaderId = MEGACells.makeId("block/" + name);
        model.customLoader((bmb, efh) -> new CustomLoaderBuilder<>(loaderId, bmb, efh) {
        });
    }

    private void craftingModel(BlockDefinition<?> block, String name) {
        builtInBlockModel("crafting/" + name + "_formed");
        var blockModel = models().cubeAll("block/crafting/" + name, MEGACells.makeId("block/crafting/" + name));
        getVariantBuilder(block.block())
                .partialState().with(AbstractCraftingUnitBlock.FORMED, false).setModels(
                        new ConfiguredModel(blockModel))
                .partialState().with(AbstractCraftingUnitBlock.FORMED, true).setModels(
                        new ConfiguredModel(models().getBuilder("block/crafting/" + name + "_formed")));
        itemModels().getBuilder("item/" + block.id().getPath()).parent(blockModel);
    }

    private void craftingMonitorModel() {
        var unitTexture = MEGACells.makeId("block/crafting/unit");
        var monitorTexture = MEGACells.makeId("block/crafting/monitor");
        var blockModel = models().cube("block/crafting/monitor", unitTexture, unitTexture, monitorTexture, unitTexture,
                unitTexture, unitTexture).texture("particle", monitorTexture);

        builtInBlockModel("crafting/monitor_formed");
        getVariantBuilder(MEGABlocks.CRAFTING_MONITOR.block()).partialState()
                .with(AbstractCraftingUnitBlock.FORMED, false).setModels(new ConfiguredModel(blockModel)).partialState()
                .with(AbstractCraftingUnitBlock.FORMED, true)
                .setModels(new ConfiguredModel(models().getBuilder("block/crafting/monitor_formed")));
        itemModels().getBuilder("item/mega_crafting_monitor").parent(blockModel);
    }

    private void energyCell() {
        var blockBuilder = getVariantBuilder(MEGABlocks.MEGA_ENERGY_CELL.block());
        var models = new ArrayList<ModelFile>();
        for (var i = 0; i < 5; i++) {
            var model = models().cubeAll("block/mega_energy_cell_" + i,
                    MEGACells.makeId("block/mega_energy_cell_" + i));
            blockBuilder.partialState().with(EnergyCellBlock.ENERGY_STORAGE, i).setModels(new ConfiguredModel(model));
            models.add(model);
        }
        var item = itemModels().withExistingParent("item/mega_energy_cell",
                models.get(0).getLocation());
        for (var i = 1; i < models.size(); i++) {
            float fillFactor = (i - 1) / (float) (models.size() - 1);
            item.override().predicate(InitItemModelsProperties.ENERGY_FILL_LEVEL_ID, fillFactor).model(models.get(i));
        }
    }
}

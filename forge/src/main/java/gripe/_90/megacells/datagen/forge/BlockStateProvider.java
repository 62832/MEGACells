package gripe._90.megacells.datagen.forge;

import java.util.ArrayList;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.datagen.CommonModelSupplier;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.util.Utils;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, Utils.MODID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        energyCell();

        for (var block : CommonModelSupplier.CRAFTING_UNITS) {
            craftingModel(block.first, block.second);
        }
        craftingMonitor();
        patternProvider();
    }

    private void builtInBlockModel(String name) {
        var model = models().getBuilder("block/" + name);
        var loaderId = Utils.makeId("block/" + name);
        model.customLoader((bmb, efh) -> new CustomLoaderBuilder<>(loaderId, bmb, efh) {
        });
    }

    private void craftingModel(BlockDefinition<?> block, String name) {
        builtInBlockModel("crafting/" + name + "_formed");
        var blockModel = models().cubeAll("block/crafting/" + name, Utils.makeId("block/crafting/" + name));
        getVariantBuilder(block.block())
                .partialState().with(AbstractCraftingUnitBlock.FORMED, false).setModels(
                        new ConfiguredModel(blockModel))
                .partialState().with(AbstractCraftingUnitBlock.FORMED, true).setModels(
                        new ConfiguredModel(models().getBuilder("block/crafting/" + name + "_formed")));
        itemModels().getBuilder("item/" + block.id().getPath()).parent(blockModel);
    }

    private void craftingMonitor() {
        var unitTexture = Utils.makeId("block/crafting/unit");
        var monitorTexture = Utils.makeId("block/crafting/monitor");
        var blockModel = models().cube("block/crafting/monitor", unitTexture, unitTexture, monitorTexture, unitTexture,
                unitTexture, unitTexture).texture("particle", monitorTexture);

        builtInBlockModel("crafting/monitor_formed");
        getVariantBuilder(MEGABlocks.CRAFTING_MONITOR.block())
                .partialState().with(AbstractCraftingUnitBlock.FORMED, false)
                .setModels(new ConfiguredModel(blockModel))
                .partialState().with(AbstractCraftingUnitBlock.FORMED, true)
                .setModels(new ConfiguredModel(models().getBuilder("block/crafting/monitor_formed")));
        itemModels().getBuilder("item/mega_crafting_monitor").parent(blockModel);
    }

    private void energyCell() {
        var blockBuilder = getVariantBuilder(MEGABlocks.MEGA_ENERGY_CELL.block());
        var models = new ArrayList<ModelFile>();
        for (var i = 0; i < 5; i++) {
            var model = models().cubeAll("block/mega_energy_cell_" + i,
                    Utils.makeId("block/mega_energy_cell_" + i));
            blockBuilder.partialState().with(EnergyCellBlock.ENERGY_STORAGE, i).setModels(new ConfiguredModel(model));
            models.add(model);
        }
        var item = itemModels().withExistingParent("item/mega_energy_cell",
                models.get(0).getLocation());
        for (var i = 1; i < models.size(); i++) {
            float fillFactor = (i - 1) / (float) (models.size() - 1);
            item.override().predicate(AppEng.makeId("fill_level"), fillFactor).model(models.get(i));
        }
    }

    private void patternProvider() {
        var definition = MEGABlocks.MEGA_PATTERN_PROVIDER;

        var texture = Utils.makeId("block/mega_pattern_provider");
        var textureAlt = Utils.makeId("block/mega_pattern_provider_alternate");
        var textureArrow = Utils.makeId("block/mega_pattern_provider_alternate_arrow");

        var modelNormal = cubeAll(definition.block());
        var modelOriented = models().cube("block/mega_pattern_provider_oriented", textureAlt, texture, textureArrow,
                textureArrow, textureArrow, textureArrow).texture("particle", texture);

        var omnidirectional = BooleanProperty.create("omnidirectional");

        getVariantBuilder(definition.block())
                .partialState().with(omnidirectional, true)
                .setModels(new ConfiguredModel(modelNormal))
                .partialState().with(omnidirectional, false)
                .setModels(new ConfiguredModel(modelOriented, 90, 0, false));
        simpleBlockItem(definition.block(), modelNormal);
    }
}

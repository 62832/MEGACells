package ninety.megacells.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.block.crafting.AbstractCraftingUnitBlock;

import ninety.megacells.MEGACells;
import ninety.megacells.block.MEGABlocks;
import ninety.megacells.core.BlockDefinition;

public class MEGABlockModelProvider extends BlockStateProvider {

    public MEGABlockModelProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MEGACells.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        craftingModel(MEGABlocks.MEGA_CRAFTING_UNIT, "unit");
        craftingModel(MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage");
        craftingModel(MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage");
        craftingModel(MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage");
        craftingModel(MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage");
        craftingModel(MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage");
        craftingModel(MEGABlocks.CRAFTING_ACCELERATOR, "accelerator");
        builtInBlockModel("crafting/monitor_formed");
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
        getVariantBuilder(block.asBlock())
                .partialState().with(AbstractCraftingUnitBlock.FORMED, false).setModels(
                        new ConfiguredModel(blockModel))
                .partialState().with(AbstractCraftingUnitBlock.FORMED, true).setModels(
                        new ConfiguredModel(models().getBuilder("block/crafting/" + name + "_formed")));
        simpleBlockItem(block.asBlock(), blockModel);
    }
}

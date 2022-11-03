package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.datagen.CommonModelSupplier;
import gripe._90.megacells.item.cell.MEGACellType;

public class BlockModelProvider extends net.minecraftforge.client.model.generators.BlockModelProvider {

    static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public BlockModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, MEGACells.MODID, efh);
        efh.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        for (var cell : CommonModelSupplier.STORAGE_CELLS) {
            var path = "block/drive/cells/" + cell.id().getPath();
            withExistingParent(path, DRIVE_CELL).texture("cell", MEGACells.makeId(path));
        }
        for (var cell : MEGACellType.ITEM.getPortableCells()) {
            var path = "block/drive/cells/" + cell.id().getPath();
            withExistingParent(path, DRIVE_CELL).texture("cell",
                    MEGACells.makeId("block/drive/cells/portable_mega_item_cell"));
        }
        for (var cell : MEGACellType.FLUID.getPortableCells()) {
            var path = "block/drive/cells/" + cell.id().getPath();
            withExistingParent(path, DRIVE_CELL).texture("cell",
                    MEGACells.makeId("block/drive/cells/portable_mega_fluid_cell"));
        }
    }
}

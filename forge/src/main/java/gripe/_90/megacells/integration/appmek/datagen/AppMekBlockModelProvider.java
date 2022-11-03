package gripe._90.megacells.integration.appmek.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;

public class AppMekBlockModelProvider extends BlockModelProvider {

    static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public AppMekBlockModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, MEGACells.MODID, efh);
        efh.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        for (var cell : AppMekCellType.CHEMICAL.getCells()) {
            var path = "block/drive/cells/" + cell.id().getPath();
            withExistingParent(path, DRIVE_CELL).texture("cell", MEGACells.makeId(path));
        }
        for (var cell : AppMekCellType.CHEMICAL.getPortableCells()) {
            var path = "block/drive/cells/" + cell.id().getPath();
            withExistingParent(path, DRIVE_CELL).texture("cell",
                    MEGACells.makeId("block/drive/cells/portable_mega_chemical_cell"));
        }
        withExistingParent("block/drive/cells/radioactive_chemical_cell", DRIVE_CELL)
                .texture("cell", MEGACells.makeId("block/drive/cells/radioactive_chemical_cell"));
    }
}

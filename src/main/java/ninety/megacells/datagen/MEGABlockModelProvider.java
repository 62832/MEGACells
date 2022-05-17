package ninety.megacells.datagen;

import java.util.stream.Stream;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.util.MEGACellType;

public class MEGABlockModelProvider extends BlockModelProvider {

    protected static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public MEGABlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MEGACells.MODID, existingFileHelper);
        existingFileHelper.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                ChemicalCellType.TYPE.getCells().stream()).flatMap(s -> s).toList()) {
            String path = "block/drive/cells/" + cell.getRegistryName().getPath();
            withExistingParent(path, DRIVE_CELL).texture("cell", path);
        }

        String portableItemCell = "block/drive/cells/portable_mega_item_cell";
        withExistingParent(portableItemCell, DRIVE_CELL).texture("cell", portableItemCell);

        String portableFluidCell = "block/drive/cells/portable_mega_fluid_cell";
        withExistingParent(portableFluidCell, DRIVE_CELL).texture("cell", portableFluidCell);

        String portableChemicalCell = "block/drive/cells/portable_mega_chemical_cell";
        withExistingParent(portableChemicalCell, DRIVE_CELL).texture("cell", portableChemicalCell);
    }
}

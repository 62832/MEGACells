package ninety.megacells.integration.appmek.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import ninety.megacells.datagen.MEGABlockModelProvider;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.integration.appmek.MEGAMekIntegration;
import ninety.megacells.util.MEGACellsUtil;

public class MEGAMekBlockModelProvider extends MEGABlockModelProvider {

    public MEGAMekBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        if (MEGAMekIntegration.isLoaded()) {
            for (var cell : ChemicalCellType.TYPE.getCells()) {
                String path = "block/drive/cells/" + MEGACellsUtil.getItemPath(cell);
                withExistingParent(path, DRIVE_CELL).texture("cell", path);
            }
            String portableChemicalCell = "block/drive/cells/portable_mega_chemical_cell";
            withExistingParent(portableChemicalCell, DRIVE_CELL).texture("cell", portableChemicalCell);
        }
    }
}

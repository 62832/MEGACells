package ninety.megacells.integration.appmek.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import ninety.megacells.datagen.MEGAItemModelProvider;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.integration.appmek.MEGAMekIntegration;

public class MEGAMekItemModelProvider extends MEGAItemModelProvider {
    public MEGAMekItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        if (MEGAMekIntegration.isLoaded()) {
            flatSingleLayer(ChemicalCellType.TYPE.housing());
            for (var storage : ChemicalCellType.TYPE.getCells()) {
                cell(storage);
            }
            for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
                portable(portable);
            }
        }
    }
}

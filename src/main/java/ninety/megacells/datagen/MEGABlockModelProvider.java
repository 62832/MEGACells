package ninety.megacells.datagen;

import java.util.stream.Stream;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import ninety.megacells.MEGACells;
import ninety.megacells.item.MEGACellType;
import ninety.megacells.util.MEGACellsUtil;

import appeng.core.AppEng;

public class MEGABlockModelProvider extends BlockModelProvider {

    private static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public MEGABlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MEGACells.MODID, existingFileHelper);
        existingFileHelper.trackGenerated(DRIVE_CELL, MODEL);
    }

    protected void registerModels() {
        for (var cell : Stream.concat(MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream()).toList()) {
            String path = "block/drive/cells/" + MEGACellsUtil.getItemPath(cell);
            withExistingParent(path, DRIVE_CELL).texture("cell", path);
        }

        String portableItemCell = "block/drive/cells/portable_mega_item_cell";
        withExistingParent(portableItemCell, DRIVE_CELL).texture("cell", portableItemCell);

        String portableFluidCell = "block/drive/cells/portable_mega_fluid_cell";
        withExistingParent(portableFluidCell, DRIVE_CELL).texture("cell", portableFluidCell);
    }
}

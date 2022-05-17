package ninety.megacells.init;

import java.util.stream.Stream;

import appeng.api.client.StorageCellModels;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.util.MEGACellType;

public class InitCellModels {
    public static void init() {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                ChemicalCellType.TYPE.getCells().stream()).flatMap(s -> s).toList()) {
            StorageCellModels.registerModel(cell,
                    MEGACells.makeId("block/drive/cells/" + MEGAItems.getItemPath(cell)));
        }
        for (var portableItemCell : MEGACellType.ITEM.getPortableCells()) {
            StorageCellModels.registerModel(portableItemCell,
                    MEGACells.makeId("block/drive/cells/portable_mega_item_cell"));
        }
        for (var portableFluidCell : MEGACellType.FLUID.getPortableCells()) {
            StorageCellModels.registerModel(portableFluidCell,
                    MEGACells.makeId("block/drive/cells/portable_mega_fluid_cell"));
        }
        for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
            StorageCellModels.registerModel(portable,
                    MEGACells.makeId("block/drive/cells/portable_mega_item_cell"));
        }
    }
}

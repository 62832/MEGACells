package ninety.megacells.init;

import java.util.stream.Stream;

import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.util.MEGACellType;
import ninety.megacells.util.MEGACellsUtil;

import appeng.api.client.StorageCellModels;

public class InitCellModels {
    public static void init() {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                ChemicalCellType.TYPE.getCells().stream()).flatMap(s -> s).toList()) {
            StorageCellModels.registerModel(cell,
                    MEGACellsUtil.makeId("block/drive/cells/" + MEGACellsUtil.getItemPath(cell)));
        }
        for (var portableItemCell : MEGACellType.ITEM.getPortableCells()) {
            StorageCellModels.registerModel(portableItemCell,
                    MEGACellsUtil.makeId("block/drive/cells/portable_mega_item_cell"));
        }
        for (var portableFluidCell : MEGACellType.FLUID.getPortableCells()) {
            StorageCellModels.registerModel(portableFluidCell,
                    MEGACellsUtil.makeId("block/drive/cells/portable_mega_fluid_cell"));
        }
        for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
            StorageCellModels.registerModel(portable,
                    MEGACellsUtil.makeId("block/drive/cells/portable_mega_item_cell"));
        }
    }
}

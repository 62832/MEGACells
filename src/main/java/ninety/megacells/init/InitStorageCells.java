package ninety.megacells.init;

import java.util.stream.Stream;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.AppMekCellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.cell.bulk.BulkCellHandler;
import ninety.megacells.item.core.MEGACellType;

public class InitStorageCells {

    public static void init() {
        StorageCells.addCellHandler(BulkCellHandler.INSTANCE);
        initModels();
    }

    public static void initModels() {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                AppMekCellType.CHEMICAL.getCells().stream()).flatMap(s -> s).toList()) {
            StorageCellModels.registerModel(cell,
                    MEGACells.makeId("block/drive/cells/" + MEGACells.getItemPath(cell)));
        }
        StorageCellModels.registerModel(MEGAItems.BULK_ITEM_CELL.asItem(),
                MEGACells.makeId("block/drive/cells/bulk_item_cell"));

        for (var portableItemCell : MEGACellType.ITEM.getPortableCells()) {
            StorageCellModels.registerModel(portableItemCell,
                    MEGACells.makeId("block/drive/cells/portable_mega_item_cell"));
        }
        for (var portableFluidCell : MEGACellType.FLUID.getPortableCells()) {
            StorageCellModels.registerModel(portableFluidCell,
                    MEGACells.makeId("block/drive/cells/portable_mega_fluid_cell"));
        }
        for (var portable : AppMekCellType.CHEMICAL.getPortableCells()) {
            StorageCellModels.registerModel(portable,
                    MEGACells.makeId("block/drive/cells/portable_mega_item_cell"));
        }
    }
}

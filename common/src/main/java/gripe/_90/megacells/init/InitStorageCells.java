package gripe._90.megacells.init;

import java.util.stream.Stream;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.item.cell.MEGACellType;
import gripe._90.megacells.item.cell.bulk.BulkCellHandler;

public class InitStorageCells {

    public static void init() {
        StorageCells.addCellHandler(BulkCellHandler.INSTANCE);
        initModels();
    }

    private static void initModels() {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream()).flatMap(s -> s).toList()) {
            StorageCellModels.registerModel(cell,
                    MEGACells.makeId("block/drive/cells/standard/" + cell.id().getPath()));
        }
        StorageCellModels.registerModel(MEGAItems.BULK_ITEM_CELL.asItem(),
                MEGACells.makeId("block/drive/cells/standard/bulk_item_cell"));

        for (var portableItemCell : MEGACellType.ITEM.getPortableCells()) {
            StorageCellModels.registerModel(portableItemCell,
                    MEGACells.makeId("block/drive/cells/portable/portable_mega_item_cell"));
        }
        for (var portableFluidCell : MEGACellType.FLUID.getPortableCells()) {
            StorageCellModels.registerModel(portableFluidCell,
                    MEGACells.makeId("block/drive/cells/portable/portable_mega_fluid_cell"));
        }
    }
}

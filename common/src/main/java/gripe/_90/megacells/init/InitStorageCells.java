package gripe._90.megacells.init;

import java.util.Collection;
import java.util.stream.Stream;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.MEGABulkCell;
import gripe._90.megacells.util.Utils;

public class InitStorageCells {
    public static void init() {
        Stream.of(MEGAItems.getItemCells(), MEGAItems.getItemPortables()).flatMap(Collection::stream)
                .forEach(c -> StorageCellModels.registerModel(c, Utils.makeId("block/drive/cells/mega_item_cell")));
        Stream.of(MEGAItems.getFluidCells(), MEGAItems.getFluidPortables()).flatMap(Collection::stream)
                .forEach(c -> StorageCellModels.registerModel(c, Utils.makeId("block/drive/cells/mega_fluid_cell")));

        StorageCells.addCellHandler(MEGABulkCell.HANDLER);
        StorageCellModels.registerModel(MEGAItems.BULK_ITEM_CELL, Utils.makeId("block/drive/cells/bulk_item_cell"));

        /*
         * if (Utils.PLATFORM.isModLoaded("appbot")) { Stream.of(AppBotItems.getCells(),
         * AppBotItems.getPortables()).flatMap(Collection::stream) .forEach(c -> StorageCellModels.registerModel(c,
         * Utils.makeId("block/drive/cells/mega_mana_cell"))); }
         */
    }
}

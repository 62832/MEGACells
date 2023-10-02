package gripe._90.megacells.init;

import java.util.Collection;
import java.util.stream.Stream;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.item.MEGABulkCell;

public class InitStorageCells {
    public static void init() {
        Stream.of(MEGAItems.getItemCells(), MEGAItems.getItemPortables())
                .flatMap(Collection::stream)
                .forEach(c -> StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_item_cell")));
        Stream.of(MEGAItems.getFluidCells(), MEGAItems.getFluidPortables())
                .flatMap(Collection::stream)
                .forEach(
                        c -> StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_fluid_cell")));

        StorageCells.addCellHandler(MEGABulkCell.HANDLER);
        StorageCellModels.registerModel(MEGAItems.BULK_ITEM_CELL, MEGACells.makeId("block/drive/cells/bulk_item_cell"));

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            Stream.of(AppBotItems.getCells(), AppBotItems.getPortables())
                    .flatMap(Collection::stream)
                    .forEach(c ->
                            StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_mana_cell")));
        }
    }
}

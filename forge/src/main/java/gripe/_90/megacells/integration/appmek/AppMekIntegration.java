package gripe._90.megacells.integration.appmek;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import me.ramidzkh.mekae2.AMItems;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.item.cell.RadioactiveCellHandler;
import gripe._90.megacells.util.Utils;

public final class AppMekIntegration {
    public static void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        AppMekItems.getCells().forEach(cell -> {
            Upgrades.add(AEItems.INVERTER_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, cell, 1, storageCellGroup);
        });

        AppMekItems.getPortables().forEach(portable -> {
            Upgrades.add(AEItems.INVERTER_CARD, portable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, portable, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, portable, 1, storageCellGroup);
        });

        for (var portable : List.of(
                AMItems.PORTABLE_CHEMICAL_CELL_1K,
                AMItems.PORTABLE_CHEMICAL_CELL_4K,
                AMItems.PORTABLE_CHEMICAL_CELL_16K,
                AMItems.PORTABLE_CHEMICAL_CELL_64K,
                AMItems.PORTABLE_CHEMICAL_CELL_256K)) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable.get(), 2, portableCellGroup);
        }
    }

    public static void initStorageCells() {
        Stream.of(AppMekItems.getCells(), AppMekItems.getPortables())
                .flatMap(Collection::stream)
                .forEach(c -> StorageCellModels.registerModel(c, Utils.makeId("block/drive/cells/mega_chemical_cell")));

        StorageCells.addCellHandler(RadioactiveCellHandler.INSTANCE);
        StorageCellModels.registerModel(
                AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem(),
                Utils.makeId("block/drive/cells/radioactive_chemical_cell"));
    }
}

package gripe._90.megacells.integration.appmek;

import java.util.List;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import me.ramidzkh.mekae2.AMItems;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.item.AppMekItems;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;
import gripe._90.megacells.integration.appmek.item.cell.radioactive.RadioactiveCellHandler;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;
import gripe._90.megacells.util.Services;

public final class AppMekIntegration {

    public static boolean isAppMekLoaded() {
        return Services.PLATFORM.isModLoaded("appmek");
    }

    public static void initIntegration() {
        if (isAppMekLoaded()) {
            initUpgrades();
            initStorageCells();
        }
    }

    private static void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        for (var cell : AppMekCellType.CHEMICAL.getCells()) {
            Upgrades.add(AEItems.INVERTER_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, cell, 1, storageCellGroup);
        }

        for (var portable : AppMekCellType.CHEMICAL.getPortableCells()) {
            Upgrades.add(AEItems.INVERTER_CARD, portable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, portable, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, portable, 1, storageCellGroup);
        }

        for (var portable : List.of(AMItems.PORTABLE_CHEMICAL_CELL_1K, AMItems.PORTABLE_CHEMICAL_CELL_4K,
                AMItems.PORTABLE_CHEMICAL_CELL_16K, AMItems.PORTABLE_CHEMICAL_CELL_64K,
                AMItems.PORTABLE_CHEMICAL_CELL_256K)) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable.get(), 2, portableCellGroup);
        }
    }

    private static void initStorageCells() {
        StorageCells.addCellHandler(RadioactiveCellHandler.INSTANCE);
        initCellModels();
    }

    private static void initCellModels() {
        for (var cell : AppMekCellType.CHEMICAL.getCells()) {
            StorageCellModels.registerModel(cell,
                    MEGACells.makeId("block/drive/cells/standard/" + cell.id().getPath()));
        }
        for (var portable : AppMekCellType.CHEMICAL.getPortableCells()) {
            StorageCellModels.registerModel(portable,
                    MEGACells.makeId("block/drive/cells/portable/portable_mega_chemical_cell"));
        }

        StorageCellModels.registerModel(AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem(),
                MEGACells.makeId("block/drive/cells/standard/radioactive_chemical_cell"));
    }

    public static void initItemColors(RegisterColorHandlersEvent.Item event) {
        for (var cell : AppMekCellType.CHEMICAL.getCells()) {
            event.getItemColors().register(MEGAStorageCell::getColor, cell);
        }
        for (var cell : AppMekCellType.CHEMICAL.getPortableCells()) {
            event.getItemColors().register(MEGAPortableCell::getColor, cell);
        }
        event.getItemColors().register(MEGAStorageCell::getColor, AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem());
    }
}

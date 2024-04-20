package gripe._90.megacells.integration.appmek;

import java.util.List;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import me.ramidzkh.mekae2.AMItems;

import gripe._90.megacells.definition.MEGAItems;

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
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, portable, 1, portableCellGroup);
            Upgrades.add(AEItems.VOID_CARD, portable, 1, portableCellGroup);
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
}

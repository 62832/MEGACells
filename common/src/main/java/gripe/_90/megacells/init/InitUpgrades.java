package gripe._90.megacells.init;

import java.util.List;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;

public class InitUpgrades {
    public static void init() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();
        var wirelessTerminalGroup = GuiText.WirelessTerminals.getTranslationKey();

        MEGAItems.getItemCells().forEach(itemCell -> {
            Upgrades.add(AEItems.FUZZY_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, itemCell, 1, storageCellGroup);
        });

        MEGAItems.getFluidCells().forEach(fluidCell -> {
            Upgrades.add(AEItems.INVERTER_CARD, fluidCell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, fluidCell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, fluidCell, 1, storageCellGroup);
        });

        MEGAItems.getItemPortables().forEach(itemPortable -> {
            Upgrades.add(AEItems.FUZZY_CARD, itemPortable, 1, portableCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, itemPortable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, itemPortable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemPortable, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, itemPortable, 1, storageCellGroup);
        });

        MEGAItems.getFluidPortables().forEach(fluidPortable -> {
            Upgrades.add(AEItems.INVERTER_CARD, fluidPortable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, fluidPortable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, fluidPortable, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, fluidPortable, 1, storageCellGroup);
        });

        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.WIRELESS_TERMINAL, 2, wirelessTerminalGroup);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 2, wirelessTerminalGroup);

        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.COLOR_APPLICATOR, 2);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.MATTER_CANNON, 2);

        for (var portableCell : List.of(AEItems.PORTABLE_ITEM_CELL1K, AEItems.PORTABLE_ITEM_CELL4K,
                AEItems.PORTABLE_ITEM_CELL16K, AEItems.PORTABLE_ITEM_CELL64K, AEItems.PORTABLE_ITEM_CELL256K,
                AEItems.PORTABLE_FLUID_CELL1K, AEItems.PORTABLE_FLUID_CELL4K, AEItems.PORTABLE_FLUID_CELL16K,
                AEItems.PORTABLE_FLUID_CELL64K, AEItems.PORTABLE_FLUID_CELL256K)) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portableCell, 2, portableCellGroup);
        }

        AE2WTIntegration.initUpgrades();
        AppBotIntegration.initUpgrades();
    }
}

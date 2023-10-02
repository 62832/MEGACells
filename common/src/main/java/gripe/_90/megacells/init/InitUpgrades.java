package gripe._90.megacells.init;

import java.util.List;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;

public class InitUpgrades {
    public static void init() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();
        var wirelessTerminalGroup = GuiText.WirelessTerminals.getTranslationKey();

        for (var itemCell : MEGAItems.getItemCells()) {
            Upgrades.add(AEItems.FUZZY_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, itemCell, 1, storageCellGroup);
        }

        for (var fluidCell : MEGAItems.getFluidCells()) {
            Upgrades.add(AEItems.INVERTER_CARD, fluidCell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, fluidCell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, fluidCell, 1, storageCellGroup);
        }

        for (var itemPortable : MEGAItems.getItemPortables()) {
            Upgrades.add(AEItems.FUZZY_CARD, itemPortable, 1, portableCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, itemPortable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, itemPortable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemPortable, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, itemPortable, 1, storageCellGroup);
        }

        for (var fluidPortable : MEGAItems.getFluidPortables()) {
            Upgrades.add(AEItems.INVERTER_CARD, fluidPortable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, fluidPortable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, fluidPortable, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, fluidPortable, 1, storageCellGroup);
        }

        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.WIRELESS_TERMINAL, 2, wirelessTerminalGroup);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 2, wirelessTerminalGroup);

        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.COLOR_APPLICATOR, 2);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.MATTER_CANNON, 2);

        Upgrades.add(MEGAItems.COMPRESSION_CARD, MEGAItems.BULK_ITEM_CELL, 1);

        for (var portableCell : List.of(
                AEItems.PORTABLE_ITEM_CELL1K,
                AEItems.PORTABLE_ITEM_CELL4K,
                AEItems.PORTABLE_ITEM_CELL16K,
                AEItems.PORTABLE_ITEM_CELL64K,
                AEItems.PORTABLE_ITEM_CELL256K,
                AEItems.PORTABLE_FLUID_CELL1K,
                AEItems.PORTABLE_FLUID_CELL4K,
                AEItems.PORTABLE_FLUID_CELL16K,
                AEItems.PORTABLE_FLUID_CELL64K,
                AEItems.PORTABLE_FLUID_CELL256K)) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portableCell, 2, portableCellGroup);
        }

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.AE2WTLIB)) {
            AE2WTIntegration.initUpgrades();
        }

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            AppBotIntegration.initUpgrades();
        }
    }
}

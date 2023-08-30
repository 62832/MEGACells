package gripe._90.megacells;

import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

import appeng.api.client.StorageCellModels;
import appeng.api.features.HotkeyAction;
import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.hotkeys.HotkeyActions;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.crafting.DecompressionService;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.item.MEGABulkCell;

public final class MEGACells {
    private MEGACells() {}

    public static final String MODID = "megacells";

    public static final Logger LOGGER = LoggerFactory.getLogger("MEGA Cells");

    public static final Platform PLATFORM =
            ServiceLoader.load(Platform.class).findFirst().orElseThrow();

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static void initCommon() {
        MEGAItems.init();
        MEGABlocks.init();
        MEGABlockEntities.init();

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            AppBotItems.init();
        }

        initStorageCells();

        MEGACells.PLATFORM.initCompression();
        GridServices.register(DecompressionService.class, DecompressionService.class);
    }

    private static void initStorageCells() {
        Stream.of(MEGAItems.getItemCells(), MEGAItems.getItemPortables())
                .flatMap(Collection::stream)
                .forEach(c -> StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_item_cell")));
        Stream.of(MEGAItems.getFluidCells(), MEGAItems.getFluidPortables())
                .flatMap(Collection::stream)
                .forEach(
                        c -> StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_fluid_cell")));

        StorageCells.addCellHandler(MEGABulkCell.HANDLER);
        StorageCellModels.registerModel(MEGAItems.BULK_ITEM_CELL, MEGACells.makeId("block/drive/cells/bulk_item_cell"));

        MEGAItems.getItemPortables()
                .forEach(cell -> HotkeyActions.registerPortableCell(cell, HotkeyAction.PORTABLE_ITEM_CELL));
        MEGAItems.getFluidPortables()
                .forEach(cell -> HotkeyActions.registerPortableCell(cell, HotkeyAction.PORTABLE_FLUID_CELL));

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            Stream.of(AppBotItems.getCells(), AppBotItems.getPortables())
                    .flatMap(Collection::stream)
                    .forEach(c ->
                            StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_mana_cell")));
        }
    }

    // has to be done post-registration
    public static void initUpgrades() {
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

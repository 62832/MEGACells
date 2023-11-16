package gripe._90.megacells.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import appeng.api.orientation.BlockOrientation;
import appeng.api.upgrades.Upgrades;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.client.gui.implementations.InterfaceScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.init.client.InitScreens;

import gripe._90.megacells.client.gui.CellDockScreen;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

public interface Platform {
    Loaders getLoader();

    CreativeModeTab.Builder getCreativeTabBuilder();

    boolean isAddonLoaded(Addons addon);

    default void initItems() {
        MEGAItems.init();
        MEGABlocks.init();
        MEGABlockEntities.init();

        if (isAddonLoaded(Addons.APPBOT)) {
            AppBotItems.init();
        }
    }

    default List<ItemLike> getAllCells() {
        var cells = new ArrayList<ItemLike>(MEGAItems.getItemCells());
        cells.addAll(MEGAItems.getFluidCells());
        cells.add(MEGAItems.BULK_ITEM_CELL);

        if (isAddonLoaded(Addons.APPBOT)) {
            cells.addAll(AppBotItems.getCells());
        }

        return cells;
    }

    default List<ItemLike> getAllPortables() {
        var portables = new ArrayList<ItemLike>(MEGAItems.getItemPortables());
        portables.addAll(MEGAItems.getFluidPortables());

        if (isAddonLoaded(Addons.APPBOT)) {
            portables.addAll(AppBotItems.getPortables());
        }

        return portables;
    }

    void register();

    default void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();
        var interfaceGroup = GuiText.Interface.getTranslationKey();
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

        Upgrades.add(AEItems.CRAFTING_CARD, MEGABlocks.MEGA_INTERFACE, 1, interfaceGroup);
        Upgrades.add(AEItems.CRAFTING_CARD, MEGAItems.MEGA_INTERFACE, 1, interfaceGroup);
        Upgrades.add(AEItems.FUZZY_CARD, MEGABlocks.MEGA_INTERFACE, 1, interfaceGroup);
        Upgrades.add(AEItems.FUZZY_CARD, MEGAItems.MEGA_INTERFACE, 1, interfaceGroup);

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

        if (isAddonLoaded(Addons.AE2WTLIB)) {
            AE2WTIntegration.initUpgrades();
        }

        if (isAddonLoaded(Addons.APPBOT)) {
            AppBotIntegration.initUpgrades();
        }
    }

    void initCompression();

    void initLavaTransform();

    void addVillagerTrade(ItemLike item, int cost, int quantity, int xp);

    interface Client {
        void initScreens();

        void initEnergyCellProps();

        void initCraftingUnitModels();

        void initItemColours(ItemColor color, List<ItemLike> items);

        default Runnable energyCellProps() {
            // doesn't play nice with Forge as a void return for whatever reason
            return () -> ItemProperties.register(
                    MEGABlocks.MEGA_ENERGY_CELL.asItem(), AppEng.makeId("fill_level"), (is, level, entity, i) -> {
                        var energyCell = (EnergyCellBlockItem) MEGABlocks.MEGA_ENERGY_CELL.asItem();

                        double curPower = energyCell.getAECurrentPower(is);
                        double maxPower = energyCell.getAEMaxPower(is);

                        return (float) (curPower / maxPower);
                    });
        }

        default void screens() {
            InitScreens.register(
                    MEGAMenus.MEGA_INTERFACE,
                    InterfaceScreen<MEGAInterfaceMenu>::new,
                    "/screens/megacells/mega_interface.json");
            InitScreens.register(
                    MEGAMenus.MEGA_PATTERN_PROVIDER,
                    PatternProviderScreen<MEGAPatternProviderMenu>::new,
                    "/screens/megacells/mega_pattern_provider.json");
            InitScreens.register(MEGAMenus.CELL_DOCK, CellDockScreen::new, "/screens/megacells/cell_dock.json");
        }

        BakedModel createCellModel(Item cell, BlockOrientation orientation);
    }
}

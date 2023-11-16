package gripe._90.megacells;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

import appeng.api.client.StorageCellModels;
import appeng.api.features.HotkeyAction;
import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import appeng.client.render.crafting.CraftingMonitorRenderer;
import appeng.hotkeys.HotkeyActions;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGAConfig;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.misc.DecompressionService;

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
        MEGAConfig.load();

        PLATFORM.initItems();
        PLATFORM.register();
        PLATFORM.initUpgrades();

        StorageCells.addCellHandler(BulkCellItem.HANDLER);

        MEGAItems.getItemPortables()
                .forEach(cell -> HotkeyActions.registerPortableCell(cell, HotkeyAction.PORTABLE_ITEM_CELL));
        MEGAItems.getFluidPortables()
                .forEach(cell -> HotkeyActions.registerPortableCell(cell, HotkeyAction.PORTABLE_FLUID_CELL));

        PLATFORM.initCompression();
        GridServices.register(DecompressionService.class, DecompressionService.class);

        PLATFORM.initLavaTransform();

        PLATFORM.addVillagerTrade(MEGAItems.SKY_STEEL_INGOT, 8, 3, 20);
        PLATFORM.addVillagerTrade(MEGAItems.ACCUMULATION_PROCESSOR_PRESS, 40, 1, 50);
    }

    public static class Client {
        private Client() {}

        public static final Platform.Client PLATFORM =
                ServiceLoader.load(Platform.Client.class).findFirst().orElseThrow();

        public static void initClient() {
            PLATFORM.initScreens();
            PLATFORM.initEnergyCellProps();
            PLATFORM.initCraftingUnitModels();

            Stream.of(MEGAItems.getItemCells(), MEGAItems.getItemPortables())
                    .flatMap(Collection::stream)
                    .forEach(c -> StorageCellModels.registerModel(c, makeId("block/drive/cells/mega_item_cell")));
            Stream.of(MEGAItems.getFluidCells(), MEGAItems.getFluidPortables())
                    .flatMap(Collection::stream)
                    .forEach(c -> StorageCellModels.registerModel(c, makeId("block/drive/cells/mega_fluid_cell")));

            StorageCellModels.registerModel(MEGAItems.BULK_ITEM_CELL, makeId("block/drive/cells/bulk_item_cell"));

            if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
                Stream.of(AppBotItems.getCells(), AppBotItems.getPortables())
                        .flatMap(Collection::stream)
                        .forEach(c -> StorageCellModels.registerModel(c, makeId("block/drive/cells/mega_mana_cell")));
            }

            BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);

            PLATFORM.initItemColours(BasicStorageCell::getColor, MEGACells.PLATFORM.getAllCells());
            PLATFORM.initItemColours(PortableCellItem::getColor, MEGACells.PLATFORM.getAllPortables());
        }
    }
}

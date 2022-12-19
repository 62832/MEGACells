package gripe._90.megacells.init.forge.client;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

import gripe._90.megacells.datagen.CommonModelSupplier;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.util.Utils;

public class InitItemColors {
    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitItemColors::initItemColors);
    }

    private static void initItemColors(RegisterColorHandlersEvent.Item event) {
        CommonModelSupplier.STORAGE_CELLS.forEach(c -> event.register(BasicStorageCell::getColor, c));
        CommonModelSupplier.PORTABLE_CELLS.forEach(c -> event.register(PortableCellItem::getColor, c));

        if (Utils.PLATFORM.isModLoaded("appmek")) {
            AppMekItems.getCells().forEach(c -> event.register(BasicStorageCell::getColor, c));
            AppMekItems.getPortables().forEach(c -> event.register(PortableCellItem::getColor, c));
            event.register(BasicStorageCell::getColor, AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem());
        }

        if (Utils.PLATFORM.isModLoaded("appbot")) {
            AppBotItems.getCells().forEach(c -> event.register(BasicStorageCell::getColor, c));
            AppBotItems.getPortables().forEach(c -> event.register(PortableCellItem::getColor, c));
        }
    }
}

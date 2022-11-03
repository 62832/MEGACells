package gripe._90.megacells.init.forge.client;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import gripe._90.megacells.datagen.CommonModelSupplier;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

public class InitItemColors {
    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitItemColors::initItemColors);
    }

    private static void initItemColors(RegisterColorHandlersEvent.Item event) {
        for (var cell : CommonModelSupplier.STORAGE_CELLS) {
            event.getItemColors().register(MEGAStorageCell::getColor, cell);
        }
        for (var cell : CommonModelSupplier.PORTABLE_CELLS) {
            event.getItemColors().register(MEGAPortableCell::getColor, cell);
        }
        if (AppMekIntegration.isAppMekLoaded()) {
            AppMekIntegration.initItemColors(event);
        }
    }
}

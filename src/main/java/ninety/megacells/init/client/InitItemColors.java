package ninety.megacells.init.client;

import java.util.stream.Stream;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.integration.appmek.AppMekCellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;
import ninety.megacells.item.core.MEGACellType;

public class InitItemColors {
    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitItemColors::initItemColors);
    }

    private static void initItemColors(RegisterColorHandlersEvent.Item event) {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                AppMekCellType.CHEMICAL.getCells().stream()).flatMap(s -> s).toList()) {
            event.getItemColors().register(MEGAStorageCell::getColor, cell);
        }
        event.getItemColors().register(MEGAStorageCell::getColor, MEGAItems.BULK_ITEM_CELL.asItem());

        for (var cell : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream(),
                AppMekCellType.CHEMICAL.getPortableCells().stream()).flatMap(s -> s).toList()) {
            event.getItemColors().register(MEGAPortableCell::getColor, cell);
        }
    }
}

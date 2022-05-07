package ninety.megacells.init;

import java.util.stream.Stream;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.item.MEGACellType;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;

public class MEGACellsClient {
    public static void initialize() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(MEGACellsClient::initItemColors);
    }

    private static void initItemColors(ColorHandlerEvent.Item event) {
        for (var cell : Stream.concat(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream()).toList()) {
            event.getItemColors().register(MEGAStorageCell::getColor, cell);
        }
        for (var cell : Stream.concat(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream()).toList()) {
            event.getItemColors().register(MEGAPortableCell::getColor, cell);
        }
    }
}

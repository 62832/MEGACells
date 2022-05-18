package ninety.megacells.init.client;

import java.util.stream.Stream;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.MEGACellType;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;

public class InitItemColors {
    public static void initialize() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitItemColors::initItemColors);
    }

    private static void initItemColors(ColorHandlerEvent.Item event) {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                ChemicalCellType.TYPE.getCells().stream()).flatMap(s -> s).toList()) {
            event.getItemColors().register(MEGAStorageCell::getColor, cell);
        }
        for (var cell : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream(),
                ChemicalCellType.TYPE.getPortableCells().stream()).flatMap(s -> s).toList()) {
            event.getItemColors().register(MEGAPortableCell::getColor, cell);
        }
    }
}

package gripe._90.megacells.init.forge.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.core.definitions.ItemDefinition;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.util.Utils;

public class InitItemColors {
    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitItemColors::initItemColors);
    }

    private static void initItemColors(RegisterColorHandlersEvent.Item event) {
        var cells = new ArrayList<ItemDefinition<?>>(List.of(
                MEGAItems.ITEM_CELL_1M, MEGAItems.ITEM_CELL_4M, MEGAItems.ITEM_CELL_16M, MEGAItems.ITEM_CELL_64M,
                MEGAItems.ITEM_CELL_256M, MEGAItems.FLUID_CELL_1M, MEGAItems.FLUID_CELL_4M, MEGAItems.FLUID_CELL_16M,
                MEGAItems.FLUID_CELL_64M, MEGAItems.FLUID_CELL_256M, MEGAItems.BULK_ITEM_CELL));
        var portables = new ArrayList<ItemDefinition<?>>(List.of(
                MEGAItems.PORTABLE_ITEM_CELL_1M, MEGAItems.PORTABLE_ITEM_CELL_4M, MEGAItems.PORTABLE_ITEM_CELL_16M,
                MEGAItems.PORTABLE_ITEM_CELL_64M, MEGAItems.PORTABLE_ITEM_CELL_256M, MEGAItems.PORTABLE_FLUID_CELL_1M,
                MEGAItems.PORTABLE_FLUID_CELL_4M, MEGAItems.PORTABLE_FLUID_CELL_16M, MEGAItems.PORTABLE_FLUID_CELL_64M,
                MEGAItems.PORTABLE_FLUID_CELL_256M));

        if (Utils.PLATFORM.isModLoaded("appmek")) {
            cells.addAll(AppMekItems.getCells());
            portables.addAll(AppMekItems.getPortables());
            cells.add(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);
        }

        if (Utils.PLATFORM.isModLoaded("appbot")) {
            cells.addAll(AppBotItems.getCells());
            portables.addAll(AppBotItems.getPortables());
        }

        event.register(BasicStorageCell::getColor, cells.toArray(new ItemLike[0]));
        event.register(PortableCellItem::getColor, portables.toArray(new ItemLike[0]));
    }
}

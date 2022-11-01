package gripe._90.megacells.init.client.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;

import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

@Environment(EnvType.CLIENT)
public class InitItemColors {
    public static void init(Registry itemColors) {
        itemColors.register(MEGAStorageCell::getColor, MEGAItems.ITEM_CELL_1M, MEGAItems.ITEM_CELL_4M,
                MEGAItems.ITEM_CELL_16M, MEGAItems.ITEM_CELL_64M, MEGAItems.ITEM_CELL_256M, MEGAItems.FLUID_CELL_1M,
                MEGAItems.FLUID_CELL_4M, MEGAItems.FLUID_CELL_16M, MEGAItems.FLUID_CELL_64M, MEGAItems.FLUID_CELL_256M,
                MEGAItems.BULK_ITEM_CELL);
        itemColors.register(MEGAPortableCell::getColor, MEGAItems.PORTABLE_ITEM_CELL_1M,
                MEGAItems.PORTABLE_ITEM_CELL_4M, MEGAItems.PORTABLE_ITEM_CELL_16M, MEGAItems.PORTABLE_ITEM_CELL_64M,
                MEGAItems.PORTABLE_ITEM_CELL_256M, MEGAItems.PORTABLE_FLUID_CELL_1M, MEGAItems.PORTABLE_FLUID_CELL_4M,
                MEGAItems.PORTABLE_FLUID_CELL_16M, MEGAItems.PORTABLE_FLUID_CELL_64M,
                MEGAItems.PORTABLE_FLUID_CELL_256M);
    }

    @FunctionalInterface
    public interface Registry {
        void register(ItemColor itemColor, ItemLike... itemLikes);
    }
}

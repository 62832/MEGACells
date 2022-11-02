package gripe._90.megacells.datagen;

import java.util.List;

import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.item.MEGAItems;

public class CommonModelSupplier {
    public static final List<ItemDefinition<?>> FLAT_ITEMS = List.of(MEGAItems.MEGA_ITEM_CELL_HOUSING,
            MEGAItems.MEGA_FLUID_CELL_HOUSING, MEGAItems.CELL_COMPONENT_1M, MEGAItems.CELL_COMPONENT_4M,
            MEGAItems.CELL_COMPONENT_16M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.CELL_COMPONENT_256M,
            MEGAItems.BULK_CELL_COMPONENT, MEGAItems.GREATER_ENERGY_CARD);

    public static final List<ItemDefinition<?>> STORAGE_CELLS = List.of(MEGAItems.ITEM_CELL_1M, MEGAItems.ITEM_CELL_4M,
            MEGAItems.ITEM_CELL_16M, MEGAItems.ITEM_CELL_64M, MEGAItems.ITEM_CELL_256M, MEGAItems.FLUID_CELL_1M,
            MEGAItems.FLUID_CELL_4M, MEGAItems.FLUID_CELL_16M, MEGAItems.FLUID_CELL_64M, MEGAItems.FLUID_CELL_256M,
            MEGAItems.BULK_ITEM_CELL);

    public static final List<ItemDefinition<?>> PORTABLE_CELLS = List.of(MEGAItems.PORTABLE_ITEM_CELL_1M,
            MEGAItems.PORTABLE_ITEM_CELL_4M, MEGAItems.PORTABLE_ITEM_CELL_16M, MEGAItems.PORTABLE_ITEM_CELL_64M,
            MEGAItems.PORTABLE_ITEM_CELL_256M, MEGAItems.PORTABLE_FLUID_CELL_1M, MEGAItems.PORTABLE_FLUID_CELL_4M,
            MEGAItems.PORTABLE_FLUID_CELL_16M, MEGAItems.PORTABLE_FLUID_CELL_64M, MEGAItems.PORTABLE_FLUID_CELL_256M);
}

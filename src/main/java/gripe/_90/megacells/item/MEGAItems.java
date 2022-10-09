package gripe._90.megacells.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.ItemDefinition;
import gripe._90.megacells.core.MEGATier;
import gripe._90.megacells.item.core.IMEGACellType;
import gripe._90.megacells.item.core.MEGACellType;

public final class MEGAItems {

    public static void init() {
        // controls static load order
    }

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    // spotless:off
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MEGACells.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(MEGAItems.ITEM_CELL_256M);
        }
    };

    public static final ItemDefinition<MaterialItem> MEGA_ITEM_CELL_HOUSING = item("mega_item_cell_housing", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> MEGA_FLUID_CELL_HOUSING = item("mega_fluid_cell_housing", MaterialItem::new);

    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_1M = component(MEGATier._1M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_4M = component(MEGATier._4M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_16M = component(MEGATier._16M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_64M = component(MEGATier._64M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_256M = component(MEGATier._256M);

    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_1M = cell(MEGATier._1M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_4M = cell(MEGATier._4M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_16M = cell(MEGATier._16M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_64M = cell(MEGATier._64M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_256M = cell(MEGATier._256M, MEGACellType.ITEM);

    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_1M = cell(MEGATier._1M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_4M = cell(MEGATier._4M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_16M = cell(MEGATier._16M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_64M = cell(MEGATier._64M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_256M = cell(MEGATier._256M, MEGACellType.FLUID);

    public static final ItemDefinition<MaterialItem> BULK_CELL_COMPONENT = item("bulk_cell_component", MaterialItem::new);
    public static final ItemDefinition<MEGABulkCell> BULK_ITEM_CELL = item("bulk_item_cell", MEGABulkCell::new);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_1M = portable(MEGATier._1M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_4M = portable(MEGATier._4M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_16M = portable(MEGATier._16M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_64M = portable(MEGATier._64M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_256M = portable(MEGATier._256M, MEGACellType.ITEM);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_1M = portable(MEGATier._1M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_4M = portable(MEGATier._4M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_16M = portable(MEGATier._16M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_64M = portable(MEGATier._64M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_256M = portable(MEGATier._256M, MEGACellType.FLUID);
    // spotless:on

    private static ItemDefinition<StorageComponentItem> component(MEGATier tier) {
        return item("cell_component_" + tier.affix, p -> new StorageComponentItem(p, tier.kbFactor()));
    }

    private static ItemDefinition<MEGAStorageCell> cell(MEGATier tier, IMEGACellType type) {
        return item(type.affix() + "_storage_cell_" + tier.affix, p -> new MEGAStorageCell(p, tier, type));
    }

    private static ItemDefinition<MEGAPortableCell> portable(MEGATier tier, IMEGACellType type) {
        return item("portable_" + type.affix() + "_cell_" + tier.affix, p -> new MEGAPortableCell(p, tier, type));
    }

    private static <T extends Item> ItemDefinition<T> item(String id, Function<Item.Properties, T> factory) {
        Item.Properties p = new Item.Properties().tab(CREATIVE_TAB);
        T item = factory.apply(p);

        ItemDefinition<T> definition = new ItemDefinition<>(MEGACells.makeId(id), item);
        ITEMS.add(definition);

        return definition;
    }
}

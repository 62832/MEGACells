package gripe._90.megacells.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.EnergyCardItem;
import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.StorageTier;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.cell.IMEGACellType;
import gripe._90.megacells.item.cell.MEGACellType;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;
import gripe._90.megacells.item.cell.bulk.MEGABulkCell;

public final class MEGAItems {

    public static void init() {
        // controls static load order
    }

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
    private static final List<StorageTier> TIERS = new ArrayList<>();

    public static List<StorageTier> getTiers() {
        return Collections.unmodifiableList(TIERS);
    }

    // spotless:off
    public static final ItemDefinition<MaterialItem> MEGA_ITEM_CELL_HOUSING = housing(MEGACellType.ITEM);
    public static final ItemDefinition<MaterialItem> MEGA_FLUID_CELL_HOUSING = housing(MEGACellType.FLUID);

    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_1M = component(1);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_4M = component(4);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_16M = component(16);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_64M = component(64);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_256M = component(256);
    
    public static final StorageTier TIER_1M = tier(1, CELL_COMPONENT_1M);
    public static final StorageTier TIER_4M = tier(2, CELL_COMPONENT_4M);
    public static final StorageTier TIER_16M = tier(3, CELL_COMPONENT_16M);
    public static final StorageTier TIER_64M = tier(4, CELL_COMPONENT_64M);
    public static final StorageTier TIER_256M = tier(5, CELL_COMPONENT_256M);

    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_1M = cell(TIER_1M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_4M = cell(TIER_4M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_16M = cell(TIER_16M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_64M = cell(TIER_64M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_256M = cell(TIER_256M, MEGACellType.ITEM);

    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_1M = cell(TIER_1M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_4M = cell(TIER_4M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_16M = cell(TIER_16M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_64M = cell(TIER_64M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_256M = cell(TIER_256M, MEGACellType.FLUID);

    public static final ItemDefinition<MaterialItem> BULK_CELL_COMPONENT = item("MEGA Bulk Storage Component", "bulk_cell_component", MaterialItem::new);
    public static final ItemDefinition<MEGABulkCell> BULK_ITEM_CELL = item("MEGA Bulk Item Storage Cell", "bulk_item_cell", MEGABulkCell::new);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_1M = portable(TIER_1M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_4M = portable(TIER_4M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_16M = portable(TIER_16M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_64M = portable(TIER_64M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_256M = portable(TIER_256M, MEGACellType.ITEM);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_1M = portable(TIER_1M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_4M = portable(TIER_4M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_16M = portable(TIER_16M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_64M = portable(TIER_64M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_256M = portable(TIER_256M, MEGACellType.FLUID);

    public static final ItemDefinition<EnergyCardItem> GREATER_ENERGY_CARD = item("Greater Energy Card", "greater_energy_card", p -> new EnergyCardItem(p, 8));
    // spotless:on

    public static ItemDefinition<MaterialItem> housing(IMEGACellType type) {
        return item("MEGA " + type.affix() + " Cell Housing", "mega_" + type.affix().toLowerCase() + "_cell_housing",
                MaterialItem::new);
    }

    private static StorageTier tier(int index, ItemDefinition<StorageComponentItem> component) {
        int multiplier = (int) Math.pow(4, index - 1);
        StorageTier tier = new StorageTier(index, multiplier + "m", 1048576 * multiplier, 2.5 + 0.5 * multiplier,
                () -> Registry.ITEM.get(component.id()));

        TIERS.add(tier);
        return tier;
    }

    private static ItemDefinition<StorageComponentItem> component(int mb) {
        return item(mb + "M MEGA Storage Component", "cell_component_" + mb + "m",
                p -> new StorageComponentItem(p, mb * 1024));
    }

    public static ItemDefinition<MEGAStorageCell> cell(StorageTier tier, IMEGACellType type) {
        return item(tier.namePrefix().toUpperCase() + " MEGA " + type.affix() + " Storage Cell",
                type.affix().toLowerCase() + "_storage_cell_" + tier.namePrefix(),
                p -> new MEGAStorageCell(p, tier, type));
    }

    public static ItemDefinition<MEGAPortableCell> portable(StorageTier tier, IMEGACellType type) {
        return item(tier.namePrefix().toUpperCase() + " Portable " + type.affix() + " Cell",
                "portable_" + type.affix().toLowerCase() + "_cell_" + tier.namePrefix(),
                p -> new MEGAPortableCell(p, tier, type));
    }

    public static <T extends Item> ItemDefinition<T> item(String englishName, String id,
            Function<Item.Properties, T> factory) {
        Item.Properties p = new Item.Properties().tab(MEGACells.CREATIVE_TAB);
        T item = factory.apply(p);

        ItemDefinition<T> definition = new ItemDefinition<>(englishName, MEGACells.makeId(id), item);
        ITEMS.add(definition);

        return definition;
    }
}

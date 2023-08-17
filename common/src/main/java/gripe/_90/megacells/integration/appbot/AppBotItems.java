package gripe._90.megacells.integration.appbot;

import java.util.List;

import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;

import appbot.item.ManaCellItem;
import appbot.item.PortableManaCellItem;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;

public final class AppBotItems {
    public static void init() {
        // controls static load order
        MEGACells.LOGGER.info("Initialised Applied Botanics integration.");
    }

    public static final ItemDefinition<MaterialItem> MEGA_MANA_CELL_HOUSING =
            MEGAItems.item("MEGA Mana Cell Housing", "mega_mana_cell_housing", MaterialItem::new);

    public static final ItemDefinition<ManaCellItem> MANA_CELL_1M = cell(MEGAItems.TIER_1M);
    public static final ItemDefinition<ManaCellItem> MANA_CELL_4M = cell(MEGAItems.TIER_4M);
    public static final ItemDefinition<ManaCellItem> MANA_CELL_16M = cell(MEGAItems.TIER_16M);
    public static final ItemDefinition<ManaCellItem> MANA_CELL_64M = cell(MEGAItems.TIER_64M);
    public static final ItemDefinition<ManaCellItem> MANA_CELL_256M = cell(MEGAItems.TIER_256M);

    public static final ItemDefinition<PortableManaCellItem> PORTABLE_MANA_CELL_1M = portable(MEGAItems.TIER_1M);
    public static final ItemDefinition<PortableManaCellItem> PORTABLE_MANA_CELL_4M = portable(MEGAItems.TIER_4M);
    public static final ItemDefinition<PortableManaCellItem> PORTABLE_MANA_CELL_16M = portable(MEGAItems.TIER_16M);
    public static final ItemDefinition<PortableManaCellItem> PORTABLE_MANA_CELL_64M = portable(MEGAItems.TIER_64M);
    public static final ItemDefinition<PortableManaCellItem> PORTABLE_MANA_CELL_256M = portable(MEGAItems.TIER_256M);

    public static List<ItemDefinition<?>> getCells() {
        return List.of(MANA_CELL_1M, MANA_CELL_4M, MANA_CELL_16M, MANA_CELL_64M, MANA_CELL_256M);
    }

    public static List<ItemDefinition<?>> getPortables() {
        return List.of(
                PORTABLE_MANA_CELL_1M,
                PORTABLE_MANA_CELL_4M,
                PORTABLE_MANA_CELL_16M,
                PORTABLE_MANA_CELL_64M,
                PORTABLE_MANA_CELL_256M);
    }

    private static ItemDefinition<ManaCellItem> cell(StorageTier tier) {
        return MEGAItems.item(
                tier.namePrefix().toUpperCase() + " MEGA Mana Storage Cell",
                "mana_storage_cell_" + tier.namePrefix(),
                p -> new ManaCellItem(p, tier.componentSupplier().get(), tier.bytes() / 1024, tier.idleDrain()));
    }

    private static ItemDefinition<PortableManaCellItem> portable(StorageTier tier) {
        return MEGAItems.item(
                tier.namePrefix().toUpperCase() + " Portable Mana Cell",
                "portable_mana_cell_" + tier.namePrefix(),
                p -> new PortableManaCellItem(p, tier.bytes() / 1024, tier.idleDrain()));
    }
}

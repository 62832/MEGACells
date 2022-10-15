package gripe._90.megacells.integration.appbot;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.items.materials.MaterialItem;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.item.MEGAItems.ItemDefinition;
import gripe._90.megacells.item.MEGAPortableCell;
import gripe._90.megacells.item.MEGAStorageCell;
import gripe._90.megacells.item.core.MEGATier;

public class AppBotItems {

    public static void init() {
        // controls static load order
    }

    // spotless:off
    public static final ItemDefinition<MaterialItem> MEGA_MANA_CELL_HOUSING = item("mega_mana_cell_housing", MaterialItem::new);

    public static final ItemDefinition<MEGAStorageCell> MANA_CELL_1M = cell(MEGATier._1M);
    public static final ItemDefinition<MEGAStorageCell> MANA_CELL_4M = cell(MEGATier._4M);
    public static final ItemDefinition<MEGAStorageCell> MANA_CELL_16M = cell(MEGATier._16M);
    public static final ItemDefinition<MEGAStorageCell> MANA_CELL_64M = cell(MEGATier._64M);
    public static final ItemDefinition<MEGAStorageCell> MANA_CELL_256M = cell(MEGATier._256M);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_MANA_CELL_1M = portable(MEGATier._1M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_MANA_CELL_4M = portable(MEGATier._4M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_MANA_CELL_16M = portable(MEGATier._16M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_MANA_CELL_64M = portable(MEGATier._64M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_MANA_CELL_256M = portable(MEGATier._256M);
    // spotless:on

    private static ItemDefinition<MEGAStorageCell> cell(MEGATier tier) {
        return item(AppBotCellType.MANA.affix() + "_storage_cell_" + tier.affix,
                p -> new MEGAStorageCell(p, tier, AppBotCellType.MANA));
    }

    private static ItemDefinition<MEGAPortableCell> portable(MEGATier tier) {
        return item("portable_" + AppBotCellType.MANA.affix() + "_cell_" + tier.affix,
                p -> new MEGAPortableCell(p, tier, AppBotCellType.MANA));
    }

    private static <T extends Item> ItemDefinition<T> item(String id, Function<Item.Properties, T> factory) {
        if (AppBotIntegration.isAppBotLoaded()) {
            Item.Properties p = new Item.Properties().tab(MEGACells.CREATIVE_TAB);
            T item = factory.apply(p);

            ItemDefinition<T> definition = new ItemDefinition<>(MEGACells.makeId(id), item);
            MEGAItems.ITEMS.add(definition);

            return definition;
        }
        return null;
    }
}

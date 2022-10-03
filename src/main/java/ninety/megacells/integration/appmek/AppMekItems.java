package ninety.megacells.integration.appmek;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.items.materials.MaterialItem;

import ninety.megacells.MEGACells;
import ninety.megacells.core.ItemDefinition;
import ninety.megacells.core.MEGATier;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;

public class AppMekItems {

    public static void init() {
        // controls static load order
    }

    public static final ItemDefinition<MaterialItem> MEGA_CHEMICAL_CELL_HOUSING = item("mega_chemical_cell_housing",
            MaterialItem::new);

    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_1M = cell(MEGATier._1M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_4M = cell(MEGATier._4M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_16M = cell(MEGATier._16M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_64M = cell(MEGATier._64M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_256M = cell(MEGATier._256M);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_1M = portable(MEGATier._1M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_4M = portable(MEGATier._4M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_16M = portable(MEGATier._16M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_64M = portable(MEGATier._64M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_256M = portable(MEGATier._256M);

    private static ItemDefinition<MEGAStorageCell> cell(MEGATier tier) {
        return item(AppMekCellType.CHEMICAL.affix() + "_storage_cell_" + tier.affix,
                p -> new MEGAChemicalCell(p.stacksTo(1), tier));
    }

    private static ItemDefinition<MEGAPortableCell> portable(MEGATier tier) {
        return item("portable_" + AppMekCellType.CHEMICAL.affix() + "_cell_" + tier.affix,
                p -> new MEGAPortableChemicalCell(p.stacksTo(1), tier));
    }

    private static <T extends Item> ItemDefinition<T> item(String id, Function<Item.Properties, T> factory) {
        if (AppMekIntegration.isAppMekLoaded()) {
            Item.Properties p = new Item.Properties().tab(MEGAItems.CREATIVE_TAB);
            T item = factory.apply(p);

            ItemDefinition<T> definition = new ItemDefinition<>(MEGACells.makeId(id), item);
            MEGAItems.ITEMS.add(definition);

            return definition;
        }
        return null;
    }
}

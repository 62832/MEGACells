package gripe._90.megacells.integration.forge.appmek.item;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.forge.appmek.AppMekIntegration;
import gripe._90.megacells.integration.forge.appmek.item.cell.AppMekCellType;
import gripe._90.megacells.integration.forge.appmek.item.cell.MEGAChemicalCell;
import gripe._90.megacells.integration.forge.appmek.item.cell.MEGAPortableChemicalCell;
import gripe._90.megacells.integration.forge.appmek.item.cell.radioactive.MEGARadioactiveCell;
import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.item.MEGAItems.ItemDefinition;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

public class AppMekItems {

    public static void init() {
        // controls static load order
    }

    // spotless:off
    public static final ItemDefinition<MaterialItem> MEGA_CHEMICAL_CELL_HOUSING = item("mega_chemical_cell_housing", MaterialItem::new);

    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_1M = cell(MEGAItems.TIER_1M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_4M = cell(MEGAItems.TIER_4M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_16M = cell(MEGAItems.TIER_16M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_64M = cell(MEGAItems.TIER_64M);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_256M = cell(MEGAItems.TIER_256M);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_1M = portable(MEGAItems.TIER_1M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_4M = portable(MEGAItems.TIER_4M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_16M = portable(MEGAItems.TIER_16M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_64M = portable(MEGAItems.TIER_64M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_256M = portable(MEGAItems.TIER_256M);

    public static final ItemDefinition<MaterialItem> RADIOACTIVE_CELL_COMPONENT = item("radioactive_cell_component", MaterialItem::new);
    public static final ItemDefinition<MEGARadioactiveCell> RADIOACTIVE_CHEMICAL_CELL = item("radioactive_chemical_cell", MEGARadioactiveCell::new);
    // spotless:on

    private static ItemDefinition<MEGAStorageCell> cell(StorageTier tier) {
        return item(AppMekCellType.CHEMICAL.affix() + "_storage_cell_" + tier.namePrefix(),
                p -> new MEGAChemicalCell(p, tier));
    }

    private static ItemDefinition<MEGAPortableCell> portable(StorageTier tier) {
        return item("portable_" + AppMekCellType.CHEMICAL.affix() + "_cell_" + tier.namePrefix(),
                p -> new MEGAPortableChemicalCell(p, tier));
    }

    private static <T extends Item> ItemDefinition<T> item(String id, Function<Item.Properties, T> factory) {
        if (AppMekIntegration.isAppMekLoaded()) {
            Item.Properties p = new Item.Properties().tab(MEGACells.CREATIVE_TAB);
            T item = factory.apply(p);

            ItemDefinition<T> definition = new ItemDefinition<>(MEGACells.makeId(id), item);
            MEGAItems.ITEMS.add(definition);

            return definition;
        }
        return null;
    }
}

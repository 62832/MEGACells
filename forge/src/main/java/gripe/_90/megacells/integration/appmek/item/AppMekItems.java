package gripe._90.megacells.integration.appmek.item;

import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;
import gripe._90.megacells.integration.appmek.item.cell.radioactive.MEGARadioactiveCell;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.cell.MEGAStorageCell;

public class AppMekItems {

    public static void init() {
        // controls static load order
    }

    // spotless:off
    public static final ItemDefinition<MaterialItem> MEGA_CHEMICAL_CELL_HOUSING = MEGAItems.housing(AppMekCellType.CHEMICAL);

    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_1M = MEGAItems.cell(MEGAItems.TIER_1M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_4M = MEGAItems.cell(MEGAItems.TIER_4M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_16M = MEGAItems.cell(MEGAItems.TIER_16M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_64M = MEGAItems.cell(MEGAItems.TIER_64M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_256M = MEGAItems.cell(MEGAItems.TIER_256M, AppMekCellType.CHEMICAL);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_1M = MEGAItems.portable(MEGAItems.TIER_1M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_4M = MEGAItems.portable(MEGAItems.TIER_4M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_16M = MEGAItems.portable(MEGAItems.TIER_16M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_64M = MEGAItems.portable(MEGAItems.TIER_64M, AppMekCellType.CHEMICAL);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_256M = MEGAItems.portable(MEGAItems.TIER_256M, AppMekCellType.CHEMICAL);

    public static final ItemDefinition<MaterialItem> RADIOACTIVE_CELL_COMPONENT = MEGAItems.item("MEGA Radioactive Storage Component", "radioactive_cell_component", MaterialItem::new);
    public static final ItemDefinition<MEGARadioactiveCell> RADIOACTIVE_CHEMICAL_CELL = MEGAItems.item("MEGA Radioactive Chemical Storage Cell", "radioactive_chemical_cell", MEGARadioactiveCell::new);
    // spotless:on
}

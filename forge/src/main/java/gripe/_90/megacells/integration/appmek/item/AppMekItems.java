package gripe._90.megacells.integration.appmek.item;

import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;
import gripe._90.megacells.integration.appmek.item.cell.MEGAChemicalCell;
import gripe._90.megacells.integration.appmek.item.cell.MEGAPortableChemicalCell;
import gripe._90.megacells.integration.appmek.item.cell.radioactive.MEGARadioactiveCell;

public class AppMekItems {

    public static void init() {
        // controls static load order
    }

    // spotless:off
    public static final ItemDefinition<MaterialItem> MEGA_CHEMICAL_CELL_HOUSING = MEGAItems.housing(AppMekCellType.CHEMICAL);

    public static final ItemDefinition<MEGAChemicalCell> CHEMICAL_CELL_1M = cell(MEGAItems.TIER_1M);
    public static final ItemDefinition<MEGAChemicalCell> CHEMICAL_CELL_4M = cell(MEGAItems.TIER_4M);
    public static final ItemDefinition<MEGAChemicalCell> CHEMICAL_CELL_16M = cell(MEGAItems.TIER_16M);
    public static final ItemDefinition<MEGAChemicalCell> CHEMICAL_CELL_64M = cell(MEGAItems.TIER_64M);
    public static final ItemDefinition<MEGAChemicalCell> CHEMICAL_CELL_256M = cell(MEGAItems.TIER_256M);

    public static final ItemDefinition<MEGAPortableChemicalCell> PORTABLE_CHEMICAL_CELL_1M = portable(MEGAItems.TIER_1M);
    public static final ItemDefinition<MEGAPortableChemicalCell> PORTABLE_CHEMICAL_CELL_4M = portable(MEGAItems.TIER_4M);
    public static final ItemDefinition<MEGAPortableChemicalCell> PORTABLE_CHEMICAL_CELL_16M = portable(MEGAItems.TIER_16M);
    public static final ItemDefinition<MEGAPortableChemicalCell> PORTABLE_CHEMICAL_CELL_64M = portable(MEGAItems.TIER_64M);
    public static final ItemDefinition<MEGAPortableChemicalCell> PORTABLE_CHEMICAL_CELL_256M = portable(MEGAItems.TIER_256M);

    public static final ItemDefinition<MaterialItem> RADIOACTIVE_CELL_COMPONENT = MEGAItems.item("MEGA Radioactive Storage Component", "radioactive_cell_component", MaterialItem::new);
    public static final ItemDefinition<MEGARadioactiveCell> RADIOACTIVE_CHEMICAL_CELL = MEGAItems.item("MEGA Radioactive Chemical Storage Cell", "radioactive_chemical_cell", MEGARadioactiveCell::new);
    // spotless:on

    private static ItemDefinition<MEGAChemicalCell> cell(StorageTier tier) {
        return MEGAItems.item(tier.namePrefix().toUpperCase() + " MEGA Chemical Storage Cell",
                "chemical_storage_cell_" + tier.namePrefix(), p -> new MEGAChemicalCell(p, tier));
    }

    private static ItemDefinition<MEGAPortableChemicalCell> portable(StorageTier tier) {
        return MEGAItems.item(tier.namePrefix().toUpperCase() + " Portable Chemical Cell",
                "portable_chemical_cell_" + tier.namePrefix(), p -> new MEGAPortableChemicalCell(p, tier));
    }
}

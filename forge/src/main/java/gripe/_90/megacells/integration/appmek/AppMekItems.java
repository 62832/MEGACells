package gripe._90.megacells.integration.appmek;

import java.util.List;

import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKey;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.AbstractPortableCell;

import me.ramidzkh.mekae2.AMMenus;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.item.ChemicalStorageCell;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.item.RadioactiveCellItem;
import gripe._90.megacells.item.cell.MEGAPortableCell;

public final class AppMekItems {
    public static void init() {
        // controls static load order
        MEGACells.LOGGER.info("Initialised Applied Mekanistics integration.");
    }

    public static final ItemDefinition<MaterialItem> MEGA_CHEMICAL_CELL_HOUSING =
            MEGAItems.item("MEGA Chemical Cell Housing", "mega_chemical_cell_housing", MaterialItem::new);

    public static final ItemDefinition<ChemicalStorageCell> CHEMICAL_CELL_1M = cell(MEGAItems.TIER_1M);
    public static final ItemDefinition<ChemicalStorageCell> CHEMICAL_CELL_4M = cell(MEGAItems.TIER_4M);
    public static final ItemDefinition<ChemicalStorageCell> CHEMICAL_CELL_16M = cell(MEGAItems.TIER_16M);
    public static final ItemDefinition<ChemicalStorageCell> CHEMICAL_CELL_64M = cell(MEGAItems.TIER_64M);
    public static final ItemDefinition<ChemicalStorageCell> CHEMICAL_CELL_256M = cell(MEGAItems.TIER_256M);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_1M = portable(MEGAItems.TIER_1M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_4M = portable(MEGAItems.TIER_4M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_16M = portable(MEGAItems.TIER_16M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_64M = portable(MEGAItems.TIER_64M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_256M = portable(MEGAItems.TIER_256M);

    public static final ItemDefinition<MaterialItem> RADIOACTIVE_CELL_COMPONENT =
            MEGAItems.item("MEGA Radioactive Storage Component", "radioactive_cell_component", MaterialItem::new);
    public static final ItemDefinition<RadioactiveCellItem> RADIOACTIVE_CHEMICAL_CELL = MEGAItems.item(
            "MEGA Radioactive Chemical Storage Cell", "radioactive_chemical_cell", RadioactiveCellItem::new);

    public static List<ItemDefinition<?>> getCells() {
        return List.of(CHEMICAL_CELL_1M, CHEMICAL_CELL_4M, CHEMICAL_CELL_16M, CHEMICAL_CELL_64M, CHEMICAL_CELL_256M);
    }

    public static List<ItemDefinition<? extends AbstractPortableCell>> getPortables() {
        return List.of(
                PORTABLE_CHEMICAL_CELL_1M,
                PORTABLE_CHEMICAL_CELL_4M,
                PORTABLE_CHEMICAL_CELL_16M,
                PORTABLE_CHEMICAL_CELL_64M,
                PORTABLE_CHEMICAL_CELL_256M);
    }

    private static ItemDefinition<ChemicalStorageCell> cell(StorageTier tier) {
        return MEGAItems.item(
                tier.namePrefix().toUpperCase() + " MEGA Chemical Storage Cell",
                "chemical_storage_cell_" + tier.namePrefix(),
                p -> new ChemicalStorageCell(p.stacksTo(1), tier, MEGA_CHEMICAL_CELL_HOUSING));
    }

    private static ItemDefinition<MEGAPortableCell> portable(StorageTier tier) {
        return MEGAItems.item(
                tier.namePrefix().toUpperCase() + " Portable Chemical Cell",
                "portable_chemical_cell_" + tier.namePrefix(),
                p ->
                        new MEGAPortableCell(
                                p, tier, MekanismKeyType.TYPE, AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, 0x33528D) {
                            @Override
                            public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
                                if (requestedAddition instanceof MekanismKey key) {
                                    return !ChemicalAttributeValidator.DEFAULT.process(key.getStack());
                                } else {
                                    return true;
                                }
                            }
                        });
    }
}

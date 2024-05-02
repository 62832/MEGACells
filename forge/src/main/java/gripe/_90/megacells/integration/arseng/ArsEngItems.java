package gripe._90.megacells.integration.arseng;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.AbstractPortableCell;

import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;

public class ArsEngItems {
    public static void init() {
        MEGACells.LOGGER.info("Initialised Ars Énergistique integration.");
    }

    public static final ItemDefinition<MaterialItem> MEGA_SOURCE_CELL_HOUSING =
            MEGAItems.item("MEGA Source Cell Housing", "mega_source_cell_housing", MaterialItem::new);

    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_1M = cell(MEGAItems.TIER_1M);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_4M = cell(MEGAItems.TIER_4M);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_16M = cell(MEGAItems.TIER_16M);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_64M = cell(MEGAItems.TIER_64M);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_256M = cell(MEGAItems.TIER_256M);

    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL_1M = portable(MEGAItems.TIER_1M);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL_4M = portable(MEGAItems.TIER_4M);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL_16M = portable(MEGAItems.TIER_16M);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL_64M = portable(MEGAItems.TIER_64M);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL_256M =
            portable(MEGAItems.TIER_256M);

    public static List<ItemDefinition<?>> getCells() {
        return List.of(SOURCE_CELL_1M, SOURCE_CELL_4M, SOURCE_CELL_16M, SOURCE_CELL_64M, SOURCE_CELL_256M);
    }

    public static List<ItemDefinition<? extends AbstractPortableCell>> getPortables() {
        return List.of(
                PORTABLE_SOURCE_CELL_1M,
                PORTABLE_SOURCE_CELL_4M,
                PORTABLE_SOURCE_CELL_16M,
                PORTABLE_SOURCE_CELL_64M,
                PORTABLE_SOURCE_CELL_256M);
    }

    private static ItemDefinition<SourceCellItem> cell(StorageTier tier) {
        return MEGAItems.item(
                tier.namePrefix().toUpperCase() + " MEGA Source Storage Cell",
                "source_storage_cell_" + tier.namePrefix(),
                p -> new SourceCellItem(p.stacksTo(1), tier, MEGA_SOURCE_CELL_HOUSING));
    }

    private static ItemDefinition<PortableSourceCellItem> portable(StorageTier tier) {
        return MEGAItems.item(
                tier.namePrefix().toUpperCase() + " Portable Source Cell",
                "portable_source_cell_" + tier.namePrefix(),
                p -> new PortableSourceCellItem(p.stacksTo(1), tier) {
                    @NotNull
                    @Override
                    public ResourceLocation getRecipeId() {
                        return MEGACells.makeId("cells/portable/portable_source_cell_" + tier.namePrefix());
                    }

                    @Override
                    public double getChargeRate(ItemStack stack) {
                        return super.getChargeRate(stack) * 2;
                    }

                    @Override
                    public double getAEMaxPower(ItemStack stack) {
                        return super.getAEMaxPower(stack) * 8;
                    }
                });
    }
}

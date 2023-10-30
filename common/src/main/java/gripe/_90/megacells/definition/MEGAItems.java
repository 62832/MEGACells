package gripe._90.megacells.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.Util;
import net.minecraft.world.item.Item;

import appeng.api.parts.PartModels;
import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.EnergyCardItem;
import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;
import appeng.items.materials.UpgradeCardItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.menu.me.common.MEStorageMenu;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.compression.DecompressionPatternItem;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.item.cell.MEGAPortableCell;
import gripe._90.megacells.item.part.CellDockPart;
import gripe._90.megacells.item.part.DecompressionModulePart;
import gripe._90.megacells.item.part.MEGAInterfacePart;
import gripe._90.megacells.item.part.MEGAPatternProviderPart;
import gripe._90.megacells.item.part.MEGAPatternProviderPartItem;

public final class MEGAItems {
    public static void init() {
        // controls static load order
        MEGACells.LOGGER.info("Initialised items.");
    }

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static final ItemDefinition<MaterialItem> SKY_STEEL_INGOT =
            item("Sky Steel Ingot", "sky_steel_ingot", p -> new MaterialItem(p.fireResistant()));

    public static final ItemDefinition<MaterialItem> ACCUMULATION_PROCESSOR_PRESS =
            item("Inscriber Accumulation Press", "accumulation_processor_press", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> ACCUMULATION_PROCESSOR_PRINT =
            item("Printed Accumulation Circuit", "printed_accumulation_processor", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> ACCUMULATION_PROCESSOR =
            item("Accumulation Processor", "accumulation_processor", MaterialItem::new);

    public static final ItemDefinition<MaterialItem> MEGA_ITEM_CELL_HOUSING =
            item("MEGA Item Cell Housing", "mega_item_cell_housing", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> MEGA_FLUID_CELL_HOUSING =
            item("MEGA Fluid Cell Housing", "mega_fluid_cell_housing", MaterialItem::new);

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

    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_1M = itemCell(TIER_1M);
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_4M = itemCell(TIER_4M);
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_16M = itemCell(TIER_16M);
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_64M = itemCell(TIER_64M);
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_256M = itemCell(TIER_256M);

    public static final ItemDefinition<BasicStorageCell> FLUID_CELL_1M = fluidCell(TIER_1M);
    public static final ItemDefinition<BasicStorageCell> FLUID_CELL_4M = fluidCell(TIER_4M);
    public static final ItemDefinition<BasicStorageCell> FLUID_CELL_16M = fluidCell(TIER_16M);
    public static final ItemDefinition<BasicStorageCell> FLUID_CELL_64M = fluidCell(TIER_64M);
    public static final ItemDefinition<BasicStorageCell> FLUID_CELL_256M = fluidCell(TIER_256M);

    public static final ItemDefinition<MaterialItem> BULK_CELL_COMPONENT =
            item("MEGA Bulk Storage Component", "bulk_cell_component", MaterialItem::new);
    public static final ItemDefinition<BulkCellItem> BULK_ITEM_CELL =
            item("MEGA Bulk Item Storage Cell", "bulk_item_cell", BulkCellItem::new);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_1M = itemPortable(TIER_1M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_4M = itemPortable(TIER_4M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_16M = itemPortable(TIER_16M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_64M = itemPortable(TIER_64M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_256M = itemPortable(TIER_256M);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_1M = fluidPortable(TIER_1M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_4M = fluidPortable(TIER_4M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_16M = fluidPortable(TIER_16M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_64M = fluidPortable(TIER_64M);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_256M = fluidPortable(TIER_256M);

    public static final ItemDefinition<EnergyCardItem> GREATER_ENERGY_CARD =
            item("Greater Energy Card", "greater_energy_card", p -> new EnergyCardItem(p, 8));
    public static final ItemDefinition<UpgradeCardItem> COMPRESSION_CARD =
            item("Compression Card", "compression_card", UpgradeCardItem::new);

    public static final ItemDefinition<DecompressionPatternItem> DECOMPRESSION_PATTERN =
            item("Decompression Pattern", "decompression_pattern", DecompressionPatternItem::new);

    public static final ItemDefinition<PartItem<MEGAInterfacePart>> MEGA_INTERFACE = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(MEGAInterfacePart.class));
        return item(
                "MEGA Interface",
                "cable_mega_interface",
                p -> new PartItem<>(p, MEGAInterfacePart.class, MEGAInterfacePart::new));
    });
    public static final ItemDefinition<MEGAPatternProviderPartItem> MEGA_PATTERN_PROVIDER = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(MEGAPatternProviderPart.class));
        return item("MEGA Pattern Provider", "cable_mega_pattern_provider", MEGAPatternProviderPartItem::new);
    });

    public static final ItemDefinition<PartItem<DecompressionModulePart>> DECOMPRESSION_MODULE = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(DecompressionModulePart.class));
        return item(
                "MEGA Decompression Module",
                "decompression_module",
                p -> new PartItem<>(p, DecompressionModulePart.class, DecompressionModulePart::new));
    });

    public static final ItemDefinition<PartItem<CellDockPart>> CELL_DOCK = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(CellDockPart.class));
        return item("ME Cell Dock", "cell_dock", p -> new PartItem<>(p, CellDockPart.class, CellDockPart::new));
    });

    public static List<ItemDefinition<?>> getItemCells() {
        return List.of(ITEM_CELL_1M, ITEM_CELL_4M, ITEM_CELL_16M, ITEM_CELL_64M, ITEM_CELL_256M);
    }

    public static List<ItemDefinition<?>> getFluidCells() {
        return List.of(FLUID_CELL_1M, FLUID_CELL_4M, FLUID_CELL_16M, FLUID_CELL_64M, FLUID_CELL_256M);
    }

    public static List<ItemDefinition<? extends AbstractPortableCell>> getItemPortables() {
        return List.of(
                PORTABLE_ITEM_CELL_1M,
                PORTABLE_ITEM_CELL_4M,
                PORTABLE_ITEM_CELL_16M,
                PORTABLE_ITEM_CELL_64M,
                PORTABLE_ITEM_CELL_256M);
    }

    public static List<ItemDefinition<? extends AbstractPortableCell>> getFluidPortables() {
        return List.of(
                PORTABLE_FLUID_CELL_1M,
                PORTABLE_FLUID_CELL_4M,
                PORTABLE_FLUID_CELL_16M,
                PORTABLE_FLUID_CELL_64M,
                PORTABLE_FLUID_CELL_256M);
    }

    private static StorageTier tier(int index, ItemDefinition<StorageComponentItem> component) {
        int multiplier = (int) Math.pow(4, index - 1);
        return new StorageTier(
                index, multiplier + "m", 1048576 * multiplier, 2.5 + 0.5 * multiplier, component::asItem);
    }

    private static ItemDefinition<StorageComponentItem> component(int mb) {
        return item(
                mb + "M MEGA Storage Component",
                "cell_component_" + mb + "m",
                p -> new StorageComponentItem(p, mb * 1024));
    }

    private static ItemDefinition<BasicStorageCell> itemCell(StorageTier tier) {
        return item(
                tier.namePrefix().toUpperCase() + " MEGA Item Storage Cell",
                "item_storage_cell_" + tier.namePrefix(),
                p -> new BasicStorageCell(
                        p.stacksTo(1),
                        tier.componentSupplier().get(),
                        MEGA_ITEM_CELL_HOUSING,
                        tier.idleDrain(),
                        tier.bytes() / 1024,
                        tier.bytes() / 128,
                        63,
                        AEKeyType.items()));
    }

    private static ItemDefinition<BasicStorageCell> fluidCell(StorageTier tier) {
        return item(
                tier.namePrefix().toUpperCase() + " MEGA Fluid Storage Cell",
                "fluid_storage_cell_" + tier.namePrefix(),
                p -> new BasicStorageCell(
                        p.stacksTo(1),
                        tier.componentSupplier().get(),
                        MEGA_FLUID_CELL_HOUSING,
                        tier.idleDrain(),
                        tier.bytes() / 1024,
                        tier.bytes() / 128,
                        18,
                        AEKeyType.fluids()));
    }

    private static ItemDefinition<MEGAPortableCell> itemPortable(StorageTier tier) {
        return item(
                tier.namePrefix().toUpperCase() + " Portable Item Cell",
                "portable_item_cell_" + tier.namePrefix(),
                p -> new MEGAPortableCell(p, tier, AEKeyType.items(), MEStorageMenu.PORTABLE_ITEM_CELL_TYPE, 0x353535));
    }

    private static ItemDefinition<MEGAPortableCell> fluidPortable(StorageTier tier) {
        return item(
                tier.namePrefix().toUpperCase() + " Portable Fluid Cell",
                "portable_fluid_cell_" + tier.namePrefix(),
                p -> new MEGAPortableCell(p, tier, AEKeyType.fluids(), MEStorageMenu.PORTABLE_FLUID_CELL_TYPE, 0xF1C5));
    }

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, MEGACells.makeId(id), factory.apply(new Item.Properties()));
        ITEMS.add(definition);
        return definition;
    }
}

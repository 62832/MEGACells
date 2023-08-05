package gripe._90.megacells.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.block.crafting.CraftingMonitorBlock;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.BlockDefinition;
import appeng.decorative.AEDecorativeBlock;

import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.item.MEGACraftingBlockItem;
import gripe._90.megacells.item.MEGAPatternProviderBlockItem;
import gripe._90.megacells.util.Utils;

public final class MEGABlocks {
    public static void init() {
        // controls static load order
        Utils.LOGGER.info("Initialised blocks.");
    }

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    private static final BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
            .strength(2.2f, 11.0f)
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .forceSolidOn();

    public static final BlockDefinition<AEDecorativeBlock> SKY_STEEL_BLOCK = block(
            "Sky Steel Block",
            "sky_steel_block",
            () -> new AEDecorativeBlock(BlockBehaviour.Properties.of()
                    .strength(5.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)));

    public static final BlockDefinition<EnergyCellBlock> MEGA_ENERGY_CELL = block(
            "Superdense Energy Cell",
            "mega_energy_cell",
            () -> new EnergyCellBlock(12800000, 3200, 12800),
            EnergyCellBlockItem::new);

    public static final BlockDefinition<CraftingUnitBlock> MEGA_CRAFTING_UNIT =
            block("MEGA Crafting Unit", "mega_crafting_unit", () -> new CraftingUnitBlock(MEGACraftingUnitType.UNIT));
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_ACCELERATOR = craftingBlock(
            "MEGA Crafting Co-Processing Unit",
            "mega_crafting_accelerator",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.ACCELERATOR),
            () -> AEItems.ENGINEERING_PROCESSOR);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_1M = craftingBlock(
            "1M MEGA Crafting Storage",
            "1m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_1M),
            () -> MEGAItems.CELL_COMPONENT_1M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_4M = craftingBlock(
            "4M MEGA Crafting Storage",
            "4m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_4M),
            () -> MEGAItems.CELL_COMPONENT_4M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_16M = craftingBlock(
            "16M MEGA Crafting Storage",
            "16m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_16M),
            () -> MEGAItems.CELL_COMPONENT_16M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_64M = craftingBlock(
            "64M MEGA Crafting Storage",
            "64m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_64M),
            () -> MEGAItems.CELL_COMPONENT_64M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_256M = craftingBlock(
            "256M MEGA Crafting Storage",
            "256m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_256M),
            () -> MEGAItems.CELL_COMPONENT_256M);
    public static final BlockDefinition<CraftingMonitorBlock> CRAFTING_MONITOR = craftingBlock(
            "MEGA Crafting Monitor",
            "mega_crafting_monitor",
            () -> new CraftingMonitorBlock(MEGACraftingUnitType.MONITOR),
            () -> AEParts.STORAGE_MONITOR);

    public static final BlockDefinition<MEGAPatternProviderBlock> MEGA_PATTERN_PROVIDER = block(
            "MEGA Pattern Provider",
            "mega_pattern_provider",
            () -> new MEGAPatternProviderBlock(props),
            MEGAPatternProviderBlockItem::new);

    private static <T extends Block> BlockDefinition<T> craftingBlock(
            String englishName, String id, Supplier<T> blockSupplier, Supplier<ItemLike> disassemblyExtra) {
        return block(
                englishName,
                id,
                blockSupplier,
                (block, props) -> new MEGACraftingBlockItem(block, props, disassemblyExtra));
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName, String id, Supplier<T> blockSupplier) {
        return block(englishName, id, blockSupplier, null);
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = blockSupplier.get();
        var itemProperties = new Item.Properties();

        BlockItem item;
        if (itemFactory != null) {
            item = itemFactory.apply(block, itemProperties);
            if (item == null) {
                throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
            }
        } else if (block instanceof AEBaseBlock) {
            item = new AEBaseBlockItem(block, itemProperties);
        } else {
            item = new BlockItem(block, itemProperties);
        }

        var definition = new BlockDefinition<>(englishName, Utils.makeId(id), block, item);
        BLOCKS.add(definition);
        return definition;
    }
}

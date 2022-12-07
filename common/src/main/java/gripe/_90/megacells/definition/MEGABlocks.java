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
import net.minecraft.world.level.material.Material;

import appeng.block.crafting.CraftingMonitorBlock;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingBlockItem;
import gripe._90.megacells.block.MEGACraftingUnitType;

public final class MEGABlocks {

    public static void init() {
        // controls static load order
    }

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    private static final BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.METAL)
            .strength(2.2f, 11.0f)
            .sound(SoundType.METAL);

    // spotless:off
    public static final BlockDefinition<EnergyCellBlock> MEGA_ENERGY_CELL = block("Superdense Energy Cell", "mega_energy_cell", () -> new EnergyCellBlock(12800000, 3200, 12800), EnergyCellBlockItem::new);

    public static final BlockDefinition<CraftingUnitBlock> MEGA_CRAFTING_UNIT = block("MEGA Crafting Unit", "mega_crafting_unit", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.UNIT), null);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_ACCELERATOR = craftingBlock("MEGA Crafting Co-Processing Unit", "mega_crafting_accelerator", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.ACCELERATOR), () -> AEItems.ENGINEERING_PROCESSOR);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_1M = craftingBlock("1M MEGA Crafting Storage", "1m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_1M), () -> MEGAItems.CELL_COMPONENT_1M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_4M = craftingBlock("4M MEGA Crafting Storage", "4m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_4M), () -> MEGAItems.CELL_COMPONENT_4M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_16M = craftingBlock("16M MEGA Crafting Storage", "16m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_16M), () -> MEGAItems.CELL_COMPONENT_16M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_64M = craftingBlock("64M MEGA Crafting Storage", "64m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_64M), () -> MEGAItems.CELL_COMPONENT_64M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_256M = craftingBlock("256M MEGA Crafting Storage", "256m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_256M), () -> MEGAItems.CELL_COMPONENT_256M);
    public static final BlockDefinition<CraftingMonitorBlock> CRAFTING_MONITOR = craftingBlock("MEGA Crafting Monitor", "mega_crafting_monitor", () -> new CraftingMonitorBlock(props, MEGACraftingUnitType.MONITOR), () -> AEParts.STORAGE_MONITOR);
    // spotless:on

    private static <T extends Block> BlockDefinition<T> craftingBlock(String englishName, String id,
            Supplier<T> blockSupplier,
            Supplier<ItemLike> disassemblyExtra) {
        return block(englishName, id, blockSupplier,
                (block, props) -> new MEGACraftingBlockItem(block, props, disassemblyExtra));
    }

    private static <T extends Block> BlockDefinition<T> block(String englishName, String id, Supplier<T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {

        // Create block and matching item
        T block = blockSupplier.get();

        Item.Properties itemProperties = new Item.Properties().tab(MEGACells.CREATIVE_TAB);

        BlockItem item;
        if (itemFactory != null) {
            item = itemFactory.apply(block, itemProperties);
            if (item == null) {
                throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
            }
        } else {
            item = new BlockItem(block, itemProperties);
        }

        BlockDefinition<T> definition = new BlockDefinition<>(englishName, MEGACells.makeId(id), block, item);

        BLOCKS.add(definition);
        return definition;
    }
}

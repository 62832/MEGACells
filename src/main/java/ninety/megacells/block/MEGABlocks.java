package ninety.megacells.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import appeng.block.crafting.CraftingMonitorBlock;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;

import ninety.megacells.MEGACells;
import ninety.megacells.core.BlockDefinition;
import ninety.megacells.item.MEGAItems;

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
    public static final BlockDefinition<MEGAEnergyCellBlock> MEGA_ENERGY_CELL = block("mega_energy_cell", MEGAEnergyCellBlock::new, MEGAEnergyCellBlockItem::new);

    public static final BlockDefinition<CraftingUnitBlock> MEGA_CRAFTING_UNIT = block("mega_crafting_unit", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.UNIT), null);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_ACCELERATOR = craftingBlock("mega_crafting_accelerator", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.ACCELERATOR), () -> AEItems.ENGINEERING_PROCESSOR);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_1M = craftingBlock("1m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_1M), () -> MEGAItems.CELL_COMPONENT_1M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_4M = craftingBlock("4m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_4M), () -> MEGAItems.CELL_COMPONENT_4M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_16M = craftingBlock("16m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_16M), () -> MEGAItems.CELL_COMPONENT_16M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_64M = craftingBlock("64m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_64M), () -> MEGAItems.CELL_COMPONENT_64M);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_256M = craftingBlock("256m_crafting_storage", () -> new CraftingUnitBlock(props, MEGACraftingUnitType.STORAGE_256M), () -> MEGAItems.CELL_COMPONENT_256M);
    public static final BlockDefinition<CraftingMonitorBlock> CRAFTING_MONITOR = craftingBlock("mega_crafting_monitor", () -> new CraftingMonitorBlock(props, MEGACraftingUnitType.MONITOR), () -> AEParts.STORAGE_MONITOR);
    // spotless:on

    private static <T extends Block> BlockDefinition<T> craftingBlock(String id, Supplier<T> blockSupplier,
            Supplier<ItemLike> disassemblyExtra) {
        return block(id, blockSupplier, (block, props) -> new MEGACraftingBlockItem(block, props, disassemblyExtra));
    }

    private static <T extends Block> BlockDefinition<T> block(
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {

        // Create block and matching item
        T block = blockSupplier.get();

        Item.Properties itemProperties = new Item.Properties().tab(MEGAItems.CREATIVE_TAB);

        BlockItem item;
        if (itemFactory != null) {
            item = itemFactory.apply(block, itemProperties);
            if (item == null) {
                throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
            }
        } else {
            item = new BlockItem(block, itemProperties);
        }

        BlockDefinition<T> definition = new BlockDefinition<>(MEGACells.makeId(id), block, item);

        BLOCKS.add(definition);
        return definition;
    }
}

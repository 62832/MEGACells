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
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseBlockItem;
import appeng.block.crafting.CraftingMonitorBlock;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.decorative.AEDecorativeBlock;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingBlockItem;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.block.MEGAInterfaceBlock;
import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.block.MEGAPatternProviderBlockItem;

public final class MEGABlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(MEGACells.MODID);

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static final BlockDefinition<AEDecorativeBlock> SKY_STEEL_BLOCK = block(
            "Sky Steel Block",
            "sky_steel_block",
            () -> new AEDecorativeBlock(BlockBehaviour.Properties.of()
                    .strength(5.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)),
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()));

    public static final BlockDefinition<EnergyCellBlock> MEGA_ENERGY_CELL = block(
            "Superdense Energy Cell",
            "mega_energy_cell",
            () -> new EnergyCellBlock(12800000, 3200, 12800),
            EnergyCellBlockItem::new);

    public static final BlockDefinition<CraftingUnitBlock> MEGA_CRAFTING_UNIT = block(
            "MEGA Crafting Unit",
            "mega_crafting_unit",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.UNIT),
            AEBaseBlockItem::new);
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

    public static final BlockDefinition<MEGAInterfaceBlock> MEGA_INTERFACE =
            block("MEGA Interface", "mega_interface", MEGAInterfaceBlock::new, AEBaseBlockItem::new);
    public static final BlockDefinition<MEGAPatternProviderBlock> MEGA_PATTERN_PROVIDER = block(
            "MEGA Pattern Provider",
            "mega_pattern_provider",
            MEGAPatternProviderBlock::new,
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
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = DR.register(id, blockSupplier);
        var item = MEGAItems.DR.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));

        var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
        BLOCKS.add(definition);
        return definition;
    }
}

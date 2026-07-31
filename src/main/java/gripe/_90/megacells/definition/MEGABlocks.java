package gripe._90.megacells.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.block.crafting.CraftingBlockItem;
import appeng.block.crafting.CraftingMonitorBlock;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.decorative.AEDecorativeBlock;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.block.MEGAInterfaceBlock;
import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.block.MEGAPatternProviderBlockItem;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.DummyIntegrationBlock;

public final class MEGABlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(MEGACells.MODID);

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static final BlockDefinition<AEDecorativeBlock> SKY_STEEL_BLOCK = block(
            "Sky Steel Block",
            "sky_steel_block",
            p -> new AEDecorativeBlock(p.strength(5.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)),
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()));
    public static final BlockDefinition<AEDecorativeBlock> SKY_BRONZE_BLOCK = block(
            "Sky Bronze Block",
            "sky_bronze_block",
            p -> new AEDecorativeBlock(p.strength(3.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)),
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()));
    public static final BlockDefinition<?> SKY_OSMIUM_BLOCK = integrationBlock(
            "Sky Osmium Block",
            "sky_osmium_block",
            p -> p.strength(7.5f, 24.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL),
            AEDecorativeBlock::new,
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()),
            Addons.APPMEK);

    public static final BlockDefinition<EnergyCellBlock> MEGA_ENERGY_CELL = block(
            "Superdense Energy Cell",
            "mega_energy_cell",
            p -> new EnergyCellBlock(AEBaseBlock.metalProps(p), 12800000, 3200, 12800),
            EnergyCellBlockItem::new);

    public static final BlockDefinition<CraftingUnitBlock> MEGA_CRAFTING_UNIT = block(
            "MEGA Crafting Unit",
            "mega_crafting_unit",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.UNIT),
            AEBaseBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_ACCELERATOR = block(
            "MEGA Crafting Co-Processing Unit",
            "mega_crafting_accelerator",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.ACCELERATOR),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_1M = block(
            "1M MEGA Crafting Storage",
            "1m_crafting_storage",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.STORAGE_1M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_4M = block(
            "4M MEGA Crafting Storage",
            "4m_crafting_storage",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.STORAGE_4M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_16M = block(
            "16M MEGA Crafting Storage",
            "16m_crafting_storage",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.STORAGE_16M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_64M = block(
            "64M MEGA Crafting Storage",
            "64m_crafting_storage",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.STORAGE_64M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_256M = block(
            "256M MEGA Crafting Storage",
            "256m_crafting_storage",
            p -> new CraftingUnitBlock(p, MEGACraftingUnitType.STORAGE_256M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingMonitorBlock> CRAFTING_MONITOR = block(
            "MEGA Crafting Monitor",
            "mega_crafting_monitor",
            p -> new CraftingMonitorBlock(p, MEGACraftingUnitType.MONITOR),
            CraftingBlockItem::new);

    public static final BlockDefinition<MEGAInterfaceBlock> MEGA_INTERFACE =
            block("MEGA Interface", "mega_interface", MEGAInterfaceBlock::new, AEBaseBlockItem::new);
    public static final BlockDefinition<MEGAPatternProviderBlock> MEGA_PATTERN_PROVIDER = block(
            "MEGA Pattern Provider",
            "mega_pattern_provider",
            MEGAPatternProviderBlock::new,
            MEGAPatternProviderBlockItem::new);

    public static final BlockDefinition<?> MEGA_EMC_INTERFACE = block(
            "MEGA Transmutation Interface",
            "mega_emc_interface",
            p -> new DummyIntegrationBlock(AEBaseBlock.metalProps(p)),
            (b, p) -> new DummyIntegrationBlock.Item(b, p, Addons.APPLIEDE));

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Function<BlockBehaviour.Properties, T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = DR.registerBlock(id, blockSupplier);
        var item = MEGAItems.DR.registerItem(id, p -> itemFactory.apply(block.get(), p.useBlockDescriptionPrefix()));

        var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
        BLOCKS.add(definition);
        return definition;
    }

    private static BlockDefinition<?> integrationBlock(
            String englishName,
            String id,
            Function<BlockBehaviour.Properties, BlockBehaviour.Properties> props,
            Function<BlockBehaviour.Properties, Block> blockFactory,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory,
            Addons addon) {
        if (addon.isLoaded()) {
            return block(englishName, id, p -> blockFactory.apply(props.apply(p)), itemFactory);
        }

        return block(
                englishName,
                id,
                p -> new DummyIntegrationBlock(props.apply(p)),
                (b, p) -> new DummyIntegrationBlock.Item(b, p, addon));
    }
}

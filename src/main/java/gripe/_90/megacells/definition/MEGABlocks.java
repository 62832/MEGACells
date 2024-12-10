package gripe._90.megacells.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

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
            () -> new AEDecorativeBlock(BlockBehaviour.Properties.of()
                    .strength(5.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)),
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()));
    public static final BlockDefinition<AEDecorativeBlock> SKY_BRONZE_BLOCK = block(
            "Sky Bronze Block",
            "sky_bronze_block",
            () -> new AEDecorativeBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 12.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)),
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()));
    public static final BlockDefinition<?> SKY_OSMIUM_BLOCK = integrationBlock(
            "Sky Osmium Block",
            "sky_osmium_block",
            () -> AEDecorativeBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(7.5f, 24.0f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL),
            (b, p) -> new AEBaseBlockItem(b, p.fireResistant()),
            Addons.APPMEK);

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
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_ACCELERATOR = block(
            "MEGA Crafting Co-Processing Unit",
            "mega_crafting_accelerator",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.ACCELERATOR),
            (block, props) -> new CraftingBlockItem(block, props) {
                @Override
                public void addCheckedInformation(
                        ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag adv) {
                    lines.add(MEGATranslations.AcceleratorThreads.text());
                }
            });
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_1M = block(
            "1M MEGA Crafting Storage",
            "1m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_1M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_4M = block(
            "4M MEGA Crafting Storage",
            "4m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_4M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_16M = block(
            "16M MEGA Crafting Storage",
            "16m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_16M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_64M = block(
            "64M MEGA Crafting Storage",
            "64m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_64M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingUnitBlock> CRAFTING_STORAGE_256M = block(
            "256M MEGA Crafting Storage",
            "256m_crafting_storage",
            () -> new CraftingUnitBlock(MEGACraftingUnitType.STORAGE_256M),
            CraftingBlockItem::new);
    public static final BlockDefinition<CraftingMonitorBlock> CRAFTING_MONITOR = block(
            "MEGA Crafting Monitor",
            "mega_crafting_monitor",
            () -> new CraftingMonitorBlock(MEGACraftingUnitType.MONITOR),
            CraftingBlockItem::new);

    public static final BlockDefinition<MEGAInterfaceBlock> MEGA_INTERFACE =
            block("MEGA Interface", "mega_interface", MEGAInterfaceBlock::new, AEBaseBlockItem::new);
    public static final BlockDefinition<MEGAPatternProviderBlock> MEGA_PATTERN_PROVIDER = block(
            "MEGA Pattern Provider",
            "mega_pattern_provider",
            MEGAPatternProviderBlock::new,
            MEGAPatternProviderBlockItem::new);

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

    private static BlockDefinition<?> integrationBlock(
            String englishName,
            String id,
            Supplier<Function<BlockBehaviour.Properties, Block>> blockFactory,
            BlockBehaviour.Properties blockProps,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory,
            Addons addon) {
        if (!addon.isLoaded()) {
            return block(
                    englishName,
                    id,
                    () -> new DummyIntegrationBlock(blockProps),
                    (b, p) -> new DummyIntegrationBlock.Item(b, p, addon));
        }

        return block(englishName, id, () -> blockFactory.get().apply(blockProps), itemFactory);
    }
}

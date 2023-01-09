package gripe._90.megacells.datagen;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.ibm.icu.impl.Pair;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;

public class CommonModelSupplier {
    public static final List<ItemDefinition<?>> FLAT_ITEMS = Lists.newArrayList(MEGAItems.MEGA_ITEM_CELL_HOUSING,
            MEGAItems.MEGA_FLUID_CELL_HOUSING, MEGAItems.CELL_COMPONENT_1M, MEGAItems.CELL_COMPONENT_4M,
            MEGAItems.CELL_COMPONENT_16M, MEGAItems.CELL_COMPONENT_64M, MEGAItems.CELL_COMPONENT_256M,
            MEGAItems.BULK_CELL_COMPONENT, MEGAItems.GREATER_ENERGY_CARD, MEGAItems.COMPRESSION_CARD,
            AppBotItems.MEGA_MANA_CELL_HOUSING);

    public static final List<ItemDefinition<?>> STORAGE_CELLS = Stream.concat(
            Stream.of(MEGAItems.getItemCells(), MEGAItems.getFluidCells(), AppBotItems.getCells())
                    .flatMap(Collection::stream),
            Stream.of(MEGAItems.BULK_ITEM_CELL)).collect(Collectors.toList());

    public static final List<ItemDefinition<?>> PORTABLE_CELLS = Stream.concat(
            Stream.of(MEGAItems.getItemPortables(), MEGAItems.getFluidPortables(), AppBotItems.getPortables())
                    .flatMap(Collection::stream),
            Stream.of()).collect(Collectors.toList());

    public static final List<Pair<BlockDefinition<?>, String>> CRAFTING_UNITS = List.of(
            Pair.of(MEGABlocks.MEGA_CRAFTING_UNIT, "unit"),
            Pair.of(MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage"),
            Pair.of(MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage"),
            Pair.of(MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage"),
            Pair.of(MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage"),
            Pair.of(MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage"),
            Pair.of(MEGABlocks.CRAFTING_ACCELERATOR, "accelerator"));
}

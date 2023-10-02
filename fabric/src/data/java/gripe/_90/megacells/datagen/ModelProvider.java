package gripe._90.megacells.datagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;

import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.util.Utils;

class ModelProvider extends FabricModelProvider {
    private static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    private static final TextureSlot CELL_TEXTURE = TextureSlot.create("cell");

    private static final ModelTemplate CELL = new ModelTemplate(
            Optional.of(new ResourceLocation("item/generated")), Optional.empty(), TextureSlot.LAYER0, LAYER1);
    private static final ModelTemplate DRIVE_CELL =
            new ModelTemplate(Optional.of(AppEng.makeId("block/drive/drive_cell")), Optional.empty(), CELL_TEXTURE);

    private static final TextureSlot SIDES = TextureSlot.create("sides");
    private static final TextureSlot SIDES_STATUS = TextureSlot.create("sidesStatus");

    private static final ModelTemplate PATTERN_PROVIDER = new ModelTemplate(
            Optional.of(AppEng.makeId("part/pattern_provider_base")),
            Optional.empty(),
            SIDES,
            SIDES_STATUS,
            TextureSlot.BACK,
            TextureSlot.FRONT,
            TextureSlot.PARTICLE);

    private static final ModelTemplate CABLE_PATTERN_PROVIDER = new ModelTemplate(
            Optional.of(AppEng.makeId("item/cable_interface")),
            Optional.empty(),
            SIDES,
            TextureSlot.BACK,
            TextureSlot.FRONT);

    ModelProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        createEnergyCell(generator);

        var craftingUnits = List.of(
                Pair.of(MEGABlocks.MEGA_CRAFTING_UNIT, "unit"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage"),
                Pair.of(MEGABlocks.CRAFTING_ACCELERATOR, "accelerator"));

        for (var block : craftingUnits) {
            createCraftingUnit(block.first.block(), block.second, generator);
        }

        createCraftingMonitor(generator);
        createPatternProviderBlock(generator);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generatePartModels(generator);

        generator.generateFlatItem(MEGAItems.MEGA_ITEM_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.MEGA_FLUID_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(AppBotItems.MEGA_MANA_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_1M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_4M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_16M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_64M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_256M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.BULK_CELL_COMPONENT.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.GREATER_ENERGY_CARD.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.COMPRESSION_CARD.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(
                MEGAItems.DECOMPRESSION_PATTERN.asItem(), AEItems.CRAFTING_PATTERN.asItem(), ModelTemplates.FLAT_ITEM);

        var cells = Stream.concat(
                        Stream.of(MEGAItems.getItemCells(), MEGAItems.getFluidCells(), AppBotItems.getCells())
                                .flatMap(Collection::stream),
                        Stream.of(MEGAItems.BULK_ITEM_CELL))
                .toList();

        for (var cell : cells) {
            createCellItem(
                    Utils.makeId("item/cell/standard/" + cell.id().getPath()),
                    Utils.makeId("item/" + cell.id().getPath()),
                    AppEng.makeId("item/storage_cell_led"),
                    generator.output);
        }

        var portables = Stream.concat(
                        Stream.of(
                                        MEGAItems.getItemPortables(),
                                        MEGAItems.getFluidPortables(),
                                        AppBotItems.getPortables())
                                .flatMap(Collection::stream),
                        Stream.of())
                .toList();

        for (var cell : portables) {
            createCellItem(
                    Utils.makeId("item/cell/portable/" + cell.id().getPath()),
                    Utils.makeId("item/" + cell.id().getPath()),
                    AppEng.makeId("item/portable_cell_led"),
                    generator.output);
        }

        createDriveCellModel("mega_item_cell", generator.output);
        createDriveCellModel("mega_fluid_cell", generator.output);
        createDriveCellModel("mega_mana_cell", generator.output);
        createDriveCellModel("bulk_item_cell", generator.output);
    }

    private void generatePartModels(ItemModelGenerators generator) {
        createPatternProviderPart(generator);
    }

    private TextureMapping cell(ResourceLocation cell, ResourceLocation led) {
        return new TextureMapping().put(TextureSlot.LAYER0, cell).put(LAYER1, led);
    }

    private TextureMapping driveCell(ResourceLocation cell) {
        return new TextureMapping().put(CELL_TEXTURE, cell);
    }

    private void createCellItem(
            ResourceLocation cellTexture,
            ResourceLocation cellModel,
            ResourceLocation led,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        CELL.create(cellModel, cell(cellTexture, led), output);
    }

    private void createDriveCellModel(String texture, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var path = Utils.makeId("block/drive/cells/" + texture);
        DRIVE_CELL.create(path, driveCell(path), output);
    }

    private MultiVariantGenerator craftingUnit(
            Block block, String texture, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var formed = Utils.makeId("block/crafting/" + texture + "_formed");
        var unformed = Utils.makeId("block/crafting/" + texture);
        return MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(
                                false,
                                Variant.variant()
                                        .with(
                                                VariantProperties.MODEL,
                                                ModelTemplates.CUBE_ALL.create(
                                                        unformed, TextureMapping.cube(unformed), output)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, formed)));
    }

    private void createCraftingUnit(Block block, String texture, BlockModelGenerators generator) {
        var formed = Utils.makeId("block/crafting/" + texture + "_formed");
        var unformed = Utils.makeId("block/crafting/" + texture);
        generator.blockStateOutput.accept(craftingUnit(block, texture, generator.modelOutput));
        generator.modelOutput.accept(formed, () -> customModelLoader(formed));
        generator.delegateItemModel(block, unformed);
    }

    private void createCraftingMonitor(BlockModelGenerators generator) {
        var formed = Utils.makeId("block/crafting/monitor_formed");
        var unformed = Utils.makeId("block/crafting/monitor");
        var unit = Utils.makeId("block/crafting/unit");
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.CRAFTING_MONITOR.block())
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(
                                false,
                                Variant.variant()
                                        .with(
                                                VariantProperties.MODEL,
                                                ModelTemplates.CUBE.create(
                                                        unformed,
                                                        new TextureMapping()
                                                                .put(TextureSlot.NORTH, unformed)
                                                                .put(TextureSlot.EAST, unit)
                                                                .put(TextureSlot.SOUTH, unit)
                                                                .put(TextureSlot.WEST, unit)
                                                                .put(TextureSlot.DOWN, unit)
                                                                .put(TextureSlot.UP, unit)
                                                                .put(TextureSlot.PARTICLE, unformed),
                                                        generator.modelOutput)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, formed))));
        generator.modelOutput.accept(formed, () -> customModelLoader(formed));
        generator.delegateItemModel(MEGABlocks.CRAFTING_MONITOR.block(), unformed);
    }

    private JsonObject customModelLoader(ResourceLocation loc) {
        var json = new JsonObject();
        json.addProperty("loader", loc.toString());
        return json;
    }

    private void createPatternProviderBlock(BlockModelGenerators generator) {
        var normal = Utils.makeId("block/mega_pattern_provider");

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.MEGA_PATTERN_PROVIDER.block())
                .with(PropertyDispatch.property(MEGAPatternProviderBlock.OMNIDIRECTIONAL)
                        .select(
                                true,
                                Variant.variant()
                                        .with(
                                                VariantProperties.MODEL,
                                                ModelTemplates.CUBE_ALL.create(
                                                        normal, TextureMapping.cube(normal), generator.modelOutput)))
                        .select(
                                false,
                                Variant.variant()
                                        .with(
                                                VariantProperties.MODEL,
                                                ModelTemplates.CUBE.create(
                                                        Utils.makeId("block/mega_pattern_provider_oriented"),
                                                        new TextureMapping()
                                                                .put(
                                                                        TextureSlot.UP,
                                                                        Utils.makeId(
                                                                                "block/mega_pattern_provider_alternate_front"))
                                                                .put(
                                                                        TextureSlot.DOWN,
                                                                        Utils.makeId(
                                                                                "block/mega_pattern_provider_alternate"))
                                                                .put(
                                                                        TextureSlot.NORTH,
                                                                        Utils.makeId(
                                                                                "block/mega_pattern_provider_alternate_arrow"))
                                                                .copySlot(TextureSlot.NORTH, TextureSlot.EAST)
                                                                .copySlot(TextureSlot.NORTH, TextureSlot.SOUTH)
                                                                .copySlot(TextureSlot.NORTH, TextureSlot.WEST)
                                                                .put(TextureSlot.PARTICLE, normal),
                                                        generator.modelOutput))
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))));

        generator.delegateItemModel(MEGABlocks.MEGA_PATTERN_PROVIDER.block(), normal);
    }

    private void createPatternProviderPart(ItemModelGenerators generator) {
        var provider = Utils.makeId("part/mega_pattern_provider");
        var monitorBack = Utils.makeId("part/mega_monitor_back");
        var monitorSides = Utils.makeId("part/mega_monitor_sides");
        PATTERN_PROVIDER.create(
                provider,
                new TextureMapping()
                        .put(SIDES_STATUS, Utils.makeId("part/mega_monitor_sides_status"))
                        .put(SIDES, monitorSides)
                        .put(TextureSlot.BACK, monitorBack)
                        .put(TextureSlot.FRONT, provider)
                        .put(TextureSlot.PARTICLE, monitorBack),
                generator.output);
        CABLE_PATTERN_PROVIDER.create(
                Utils.makeId("item/cable_mega_pattern_provider"),
                new TextureMapping()
                        .put(SIDES, monitorSides)
                        .put(TextureSlot.FRONT, provider)
                        .put(TextureSlot.BACK, monitorBack),
                generator.output);
    }

    private void createEnergyCell(BlockModelGenerators generator) {
        var cell = MEGABlocks.MEGA_ENERGY_CELL;
        var fillStage = PropertyDispatch.property(EnergyCellBlock.ENERGY_STORAGE);
        List<Pair<ResourceLocation, JsonObject>> itemModelOverrides = new ArrayList<>();

        for (var i = 0; i < 5; i++) {
            fillStage.select(
                    i,
                    Variant.variant()
                            .with(
                                    VariantProperties.MODEL,
                                    generator.createSuffixedVariant(
                                            cell.block(), "_" + i, ModelTemplates.CUBE_ALL, TextureMapping::cube)));
            if (i < 4) {
                var fillPredicate = new JsonObject();
                fillPredicate.addProperty("ae2:fill_level", 0.25 * i);
                itemModelOverrides.add(
                        Pair.of(Utils.makeId("block/" + cell.id().getPath() + "_" + (i + 1)), fillPredicate));
            }
        }

        generator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(cell.block()).with(fillStage));
        generator.modelOutput.accept(
                Utils.makeId("item/" + cell.id().getPath()),
                new OverrideableDelegatedModel(
                        Utils.makeId("block/" + cell.id().getPath() + "_0"), itemModelOverrides));
    }

    static class OverrideableDelegatedModel extends DelegatedModel {
        private final List<Pair<ResourceLocation, JsonObject>> overrides;

        public OverrideableDelegatedModel(
                ResourceLocation resourceLocation, List<Pair<ResourceLocation, JsonObject>> overrides) {
            super(resourceLocation);
            this.overrides = overrides;
        }

        @NotNull
        @Override
        public JsonElement get() {
            JsonObject json = super.get().getAsJsonObject();

            JsonArray array = new JsonArray();
            for (var override : this.overrides) {
                JsonObject entry = new JsonObject();
                entry.addProperty("model", override.first.toString());
                entry.add("predicate", override.second);
                array.add(entry);
            }

            json.add("overrides", array);
            return json;
        }
    }
}

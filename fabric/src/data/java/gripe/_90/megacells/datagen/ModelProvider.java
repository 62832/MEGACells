package gripe._90.megacells.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.icu.impl.Pair;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.blockstates.VariantProperty;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.util.Utils;

class ModelProvider extends FabricModelProvider {
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");
    private static final ResourceLocation GENERATED = new ResourceLocation("item/generated");

    private static final ModelTemplate CELL = new ModelTemplate(Optional.of(GENERATED),
            Optional.empty(), TextureSlot.LAYER0, TextureSlot.LAYER1);
    private static final ModelTemplate PORTABLE = new ModelTemplate(Optional.of(GENERATED),
            Optional.empty(), TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, LAYER3);

    private static final TextureSlot CELL_TEXTURE = TextureSlot.create("cell");
    private static final ModelTemplate DRIVE_CELL = new ModelTemplate(
            Optional.of(AppEng.makeId("block/drive/drive_cell")), Optional.empty(), CELL_TEXTURE);

    private static final TextureSlot SIDES = TextureSlot.create("sides");
    private static final TextureSlot SIDES_STATUS = TextureSlot.create("sidesStatus");

    private static final ModelTemplate PATTERN_PROVIDER = new ModelTemplate(
            Optional.of(AppEng.makeId("part/pattern_provider_base")), Optional.empty(),
            SIDES, SIDES_STATUS, TextureSlot.BACK, TextureSlot.FRONT, TextureSlot.PARTICLE);

    private static final ModelTemplate CABLE_PATTERN_PROVIDER = new ModelTemplate(
            Optional.of(AppEng.makeId("item/cable_interface")), Optional.empty(),
            SIDES, TextureSlot.BACK, TextureSlot.FRONT);

    private static final VariantProperty<VariantProperties.Rotation> Z_ROT = new VariantProperty<>("ae2:z",
            r -> new JsonPrimitive(r.ordinal() * 90));

    ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createTrivialCube(MEGABlocks.SKY_STEEL_BLOCK.block());

        energyCell(generator);

        var craftingUnits = List.of(
                Pair.of(MEGABlocks.MEGA_CRAFTING_UNIT, "unit"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage"),
                Pair.of(MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage"),
                Pair.of(MEGABlocks.CRAFTING_ACCELERATOR, "accelerator"));

        for (var block : craftingUnits) {
            craftingUnit(block.first.block(), block.second, generator);
        }

        craftingMonitor(generator);
        patternProviderBlock(generator);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(MEGAItems.SKY_STEEL_INGOT.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.ACCUMULATION_PROCESSOR_PRESS.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.ACCUMULATION_PROCESSOR_PRINT.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.ACCUMULATION_PROCESSOR.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.MEGA_ITEM_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.MEGA_FLUID_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_1M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_4M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_16M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_64M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.CELL_COMPONENT_256M.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.BULK_CELL_COMPONENT.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.GREATER_ENERGY_CARD.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.COMPRESSION_CARD.asItem(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(MEGAItems.DECOMPRESSION_PATTERN.asItem(), AEItems.CRAFTING_PATTERN.asItem(),
                ModelTemplates.FLAT_ITEM);

        var cells = new ArrayList<>(MEGAItems.getItemCells());
        cells.addAll(MEGAItems.getFluidCells());
        cells.add(MEGAItems.BULK_ITEM_CELL);

        if (Utils.PLATFORM.isModLoaded("appbot")) {
            cells.addAll(AppBotItems.getCells());
            generator.generateFlatItem(AppBotItems.MEGA_MANA_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);

            for (var portable : AppBotItems.getPortables()) {
                // lot of assumptions being made here in advance
                portableModel(portable, "mana", new ResourceLocation("appbot", "item/portable_cell_mana_housing"),
                        generator);
            }

            driveCell("mega_mana_cell", generator);
        }

        for (var cell : cells) {
            cellModel(cell, generator);
        }

        for (var portable : MEGAItems.getItemPortables()) {
            portableModel(portable, "item", AppEng.makeId("item/portable_cell_item_housing"), generator);
        }

        for (var portable : MEGAItems.getFluidPortables()) {
            portableModel(portable, "fluid", AppEng.makeId("item/portable_cell_fluid_housing"), generator);
        }

        driveCell("mega_item_cell", generator);
        driveCell("mega_fluid_cell", generator);
        driveCell("bulk_item_cell", generator);

        generatePartModels(generator);
    }

    private void generatePartModels(ItemModelGenerators generator) {
        patternProviderPart(generator);
    }

    private void cellModel(ItemDefinition<?> cell, ItemModelGenerators generator) {
        CELL.create(Utils.makeId("item/" + cell.id().getPath()), new TextureMapping()
                .put(TextureSlot.LAYER0, Utils.makeId("item/cell/standard/" + cell.id().getPath()))
                .put(TextureSlot.LAYER1, AppEng.makeId("item/storage_cell_led")), generator.output);
    }

    private void portableModel(ItemDefinition<?> portable, String screenType, ResourceLocation housingTexture,
            ItemModelGenerators generator) {
        var path = portable.id().getPath();
        var tierSuffix = path.substring(path.lastIndexOf('_') + 1);
        PORTABLE.create(Utils.makeId("item/" + portable.id().getPath()), new TextureMapping()
                .put(TextureSlot.LAYER0, Utils.makeId("item/cell/portable/portable_cell_" + screenType + "_screen"))
                .put(TextureSlot.LAYER1, AppEng.makeId("item/portable_cell_led"))
                .put(TextureSlot.LAYER2, housingTexture)
                .put(LAYER3, Utils.makeId("item/cell/portable/portable_cell_side_" + tierSuffix)),
                generator.output);
    }

    private void driveCell(String texture, ItemModelGenerators generator) {
        var path = Utils.makeId("block/drive/cells/" + texture);
        DRIVE_CELL.create(path, new TextureMapping().put(CELL_TEXTURE, path), generator.output);
    }

    private void craftingUnit(Block block, String texture, BlockModelGenerators generator) {
        var formed = Utils.makeId("block/crafting/" + texture + "_formed");
        var unformed = Utils.makeId("block/crafting/" + texture);

        var craftingUnit = MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL,
                                ModelTemplates.CUBE_ALL.create(unformed, TextureMapping.cube(unformed),
                                        generator.modelOutput)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, formed)));

        generator.blockStateOutput.accept(craftingUnit);
        generator.modelOutput.accept(formed, () -> customModelLoader(formed));
        generator.delegateItemModel(block, unformed);
    }

    private void craftingMonitor(BlockModelGenerators generator) {
        var unformed = Utils.makeId("block/crafting/monitor");
        var unit = Utils.makeId("block/crafting/unit");
        var unformedModel = ModelTemplates.CUBE.create(unformed, new TextureMapping()
                .put(TextureSlot.NORTH, unformed)
                .put(TextureSlot.EAST, unit)
                .put(TextureSlot.SOUTH, unit)
                .put(TextureSlot.WEST, unit)
                .put(TextureSlot.DOWN, unit)
                .put(TextureSlot.UP, unit)
                .put(TextureSlot.PARTICLE, unformed), generator.modelOutput);

        var formedModel = Utils.makeId("block/crafting/monitor_formed");
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.CRAFTING_MONITOR.block())
                .with(PropertyDispatch.properties(AbstractCraftingUnitBlock.FORMED, BlockStateProperties.FACING)
                        .generate((formed, facing) -> {
                            if (formed) {
                                return Variant.variant().with(VariantProperties.MODEL, formedModel);
                            } else {
                                return applyOrientation(
                                        Variant.variant().with(VariantProperties.MODEL, unformedModel),
                                        BlockOrientation.get(facing));
                            }
                        })));
        generator.modelOutput.accept(formedModel, () -> customModelLoader(formedModel));
        generator.delegateItemModel(MEGABlocks.CRAFTING_MONITOR.block(), unformed);
    }

    private JsonObject customModelLoader(ResourceLocation loc) {
        var json = new JsonObject();
        json.addProperty("loader", loc.toString());
        return json;
    }

    private void patternProviderBlock(BlockModelGenerators generator) {
        var normal = ModelTemplates.CUBE_ALL.create(MEGABlocks.MEGA_PATTERN_PROVIDER.block(),
                TextureMapping.cube(MEGABlocks.MEGA_PATTERN_PROVIDER.block()),
                generator.modelOutput);
        var oriented = ModelTemplates.CUBE
                .create(Utils.makeId("block/mega_pattern_provider_oriented"), new TextureMapping()
                        .put(TextureSlot.UP, Utils.makeId("block/mega_pattern_provider_alternate_front"))
                        .put(TextureSlot.DOWN, Utils.makeId("block/mega_pattern_provider_alternate"))
                        .put(TextureSlot.NORTH, Utils.makeId("block/mega_pattern_provider_alternate_arrow"))
                        .copySlot(TextureSlot.NORTH, TextureSlot.EAST)
                        .copySlot(TextureSlot.NORTH, TextureSlot.SOUTH)
                        .copySlot(TextureSlot.NORTH, TextureSlot.WEST)
                        .put(TextureSlot.PARTICLE, normal), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.MEGA_PATTERN_PROVIDER.block())
                .with(PropertyDispatch.property(MEGAPatternProviderBlock.PUSH_DIRECTION).generate(pushDirection -> {
                    var forward = pushDirection.getDirection();

                    if (forward == null) {
                        return Variant.variant().with(VariantProperties.MODEL, normal);
                    } else {
                        var orientation = BlockOrientation.get(forward);
                        return applyRotation(
                                Variant.variant().with(VariantProperties.MODEL, oriented),
                                // + 90 because the default model is oriented UP, while block orientation assumes NORTH
                                orientation.getAngleX() + 90,
                                orientation.getAngleY(),
                                0);
                    }
                })));

        generator.delegateItemModel(MEGABlocks.MEGA_PATTERN_PROVIDER.block(), normal);
    }

    protected Variant applyOrientation(Variant variant, BlockOrientation orientation) {
        return applyRotation(variant,
                orientation.getAngleX(),
                orientation.getAngleY(),
                orientation.getAngleZ());
    }

    protected Variant applyRotation(Variant variant, int angleX, int angleY, int angleZ) {
        angleX = normalizeAngle(angleX);
        angleY = normalizeAngle(angleY);
        angleZ = normalizeAngle(angleZ);

        if (angleX != 0) {
            variant = variant.with(VariantProperties.X_ROT, rotationByAngle(angleX));
        }
        if (angleY != 0) {
            variant = variant.with(VariantProperties.Y_ROT, rotationByAngle(angleY));
        }
        if (angleZ != 0) {
            variant = variant.with(Z_ROT, rotationByAngle(angleZ));
        }
        return variant;
    }

    private int normalizeAngle(int angle) {
        return angle - (angle / 360) * 360;
    }

    private VariantProperties.Rotation rotationByAngle(int angle) {
        return switch (angle) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Invalid angle: " + angle);
        };
    }

    private void patternProviderPart(ItemModelGenerators generator) {
        var provider = Utils.makeId("part/mega_pattern_provider");
        var monitorBack = Utils.makeId("part/mega_monitor_back");
        var monitorSides = Utils.makeId("part/mega_monitor_sides");
        PATTERN_PROVIDER.create(provider, new TextureMapping()
                .put(SIDES_STATUS, Utils.makeId("part/mega_monitor_sides_status"))
                .put(SIDES, monitorSides).put(TextureSlot.BACK, monitorBack)
                .put(TextureSlot.FRONT, provider).put(TextureSlot.PARTICLE, monitorBack),
                generator.output);
        CABLE_PATTERN_PROVIDER.create(Utils.makeId("item/cable_mega_pattern_provider"), new TextureMapping()
                .put(SIDES, monitorSides).put(TextureSlot.FRONT, provider).put(TextureSlot.BACK, monitorBack),
                generator.output);
    }

    private void energyCell(BlockModelGenerators generator) {
        var cell = MEGABlocks.MEGA_ENERGY_CELL;
        var fillStage = PropertyDispatch.property(EnergyCellBlock.ENERGY_STORAGE);
        List<Pair<ResourceLocation, JsonObject>> itemModelOverrides = new ArrayList<>();

        for (var i = 0; i < 5; i++) {
            fillStage.select(i, Variant.variant().with(VariantProperties.MODEL,
                    generator.createSuffixedVariant(cell.block(), "_" + i, ModelTemplates.CUBE_ALL,
                            TextureMapping::cube)));
            if (i < 4) {
                var fillPredicate = new JsonObject();
                fillPredicate.addProperty("ae2:fill_level", 0.25 * i);
                itemModelOverrides.add(
                        Pair.of(Utils.makeId("block/" + cell.id().getPath() + "_" + (i + 1)), fillPredicate));
            }
        }

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cell.block()).with(fillStage));
        generator.modelOutput.accept(Utils.makeId("item/" + cell.id().getPath()), new OverrideableDelegatedModel(
                Utils.makeId("block/" + cell.id().getPath() + "_0"), itemModelOverrides));
    }

    static class OverrideableDelegatedModel extends DelegatedModel {
        private final List<Pair<ResourceLocation, JsonObject>> overrides;

        public OverrideableDelegatedModel(ResourceLocation resourceLocation,
                List<Pair<ResourceLocation, JsonObject>> overrides) {
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

package gripe._90.megacells.datagen;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.blockstates.VariantProperty;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.mixin.data.TextureSlotAccessor;

public class CommonModelProvider implements DataProvider {
    private static final TextureSlot LAYER3 = TextureSlotAccessor.invokeCreate("layer3");
    private static final ModelTemplate PORTABLE = new ModelTemplate(
            Optional.of(new ResourceLocation("minecraft:item/generated")),
            Optional.empty(),
            TextureSlot.LAYER0,
            TextureSlot.LAYER1,
            TextureSlot.LAYER2,
            LAYER3);

    private static final TextureSlot CELL = TextureSlotAccessor.invokeCreate("cell");
    private static final ModelTemplate DRIVE_CELL =
            new ModelTemplate(Optional.of(AppEng.makeId("block/drive/drive_cell")), Optional.empty(), CELL);

    private static final TextureSlot SIDES = TextureSlotAccessor.invokeCreate("sides");
    private static final TextureSlot SIDES_STATUS = TextureSlotAccessor.invokeCreate("sidesStatus");
    private static final ModelTemplate INTERFACE = new ModelTemplate(
            Optional.of(AppEng.makeId("part/interface_base")),
            Optional.empty(),
            SIDES,
            SIDES_STATUS,
            TextureSlot.BACK,
            TextureSlot.FRONT,
            TextureSlot.PARTICLE);
    private static final ModelTemplate CABLE_INTERFACE = new ModelTemplate(
            Optional.of(AppEng.makeId("item/cable_interface")),
            Optional.empty(),
            SIDES,
            TextureSlot.BACK,
            TextureSlot.FRONT);

    private static final VariantProperty<VariantProperties.Rotation> Z_ROT =
            new VariantProperty<>("ae2:z", r -> new JsonPrimitive(r.ordinal() * 90));

    private final PackOutput output;

    public CommonModelProvider(PackOutput output) {
        this.output = output;
    }

    @NotNull
    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        var states = new HashMap<Block, BlockStateGenerator>();
        var models = new HashMap<ResourceLocation, Supplier<JsonElement>>();

        generateBlockStateModels(gen -> states.put(gen.getBlock(), gen), models::put);
        generateItemModels(models::put);

        var blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        var modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");

        return CompletableFuture.allOf(
                saveCollection(writer, states, b -> blockStatePathProvider.json(BuiltInRegistries.BLOCK.getKey(b))),
                saveCollection(writer, models, modelPathProvider::json));
    }

    private <T> CompletableFuture<?> saveCollection(
            CachedOutput writer, Map<T, ? extends Supplier<JsonElement>> map, Function<T, Path> function) {
        return CompletableFuture.allOf(map.keySet().stream()
                .map(key -> DataProvider.saveStable(writer, map.get(key).get(), function.apply(key)))
                .toArray(CompletableFuture[]::new));
    }

    private void generateBlockStateModels(
            Consumer<BlockStateGenerator> blockStateOutput,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        simpleBlock(MEGABlocks.SKY_STEEL_BLOCK, blockStateOutput, modelOutput);
        simpleBlock(MEGABlocks.MEGA_INTERFACE, blockStateOutput, modelOutput);

        craftingUnit(MEGABlocks.MEGA_CRAFTING_UNIT, "unit", blockStateOutput, modelOutput);
        craftingUnit(MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage", blockStateOutput, modelOutput);
        craftingUnit(MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage", blockStateOutput, modelOutput);
        craftingUnit(MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage", blockStateOutput, modelOutput);
        craftingUnit(MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage", blockStateOutput, modelOutput);
        craftingUnit(MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage", blockStateOutput, modelOutput);
        craftingUnit(MEGABlocks.CRAFTING_ACCELERATOR, "accelerator", blockStateOutput, modelOutput);

        energyCell(blockStateOutput, modelOutput);
        patternProviderBlock(blockStateOutput, modelOutput);
        craftingMonitor(blockStateOutput, modelOutput);
    }

    private void generateItemModels(BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        flatItem(MEGAItems.SKY_STEEL_INGOT, output);

        flatItem(MEGAItems.ACCUMULATION_PROCESSOR_PRESS, output);
        flatItem(MEGAItems.ACCUMULATION_PROCESSOR_PRINT, output);
        flatItem(MEGAItems.ACCUMULATION_PROCESSOR, output);

        flatItem(MEGAItems.MEGA_ITEM_CELL_HOUSING, output);
        flatItem(MEGAItems.MEGA_FLUID_CELL_HOUSING, output);

        flatItem(MEGAItems.CELL_COMPONENT_1M, output);
        flatItem(MEGAItems.CELL_COMPONENT_4M, output);
        flatItem(MEGAItems.CELL_COMPONENT_16M, output);
        flatItem(MEGAItems.CELL_COMPONENT_64M, output);
        flatItem(MEGAItems.CELL_COMPONENT_256M, output);
        flatItem(MEGAItems.BULK_CELL_COMPONENT, output);

        flatItem(MEGAItems.GREATER_ENERGY_CARD, output);
        flatItem(MEGAItems.COMPRESSION_CARD, output);

        ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(MEGAItems.DECOMPRESSION_PATTERN.asItem()),
                TextureMapping.layer0(AEItems.CRAFTING_PATTERN.asItem()),
                output);

        MEGAItems.getItemCells().forEach(cell -> cellModel(cell, output));
        MEGAItems.getFluidCells().forEach(cell -> cellModel(cell, output));
        cellModel(MEGAItems.BULK_ITEM_CELL, output);

        for (var portable : MEGAItems.getItemPortables()) {
            portableModel(portable, "item", AppEng.makeId("item/portable_cell_item_housing"), output);
        }

        for (var portable : MEGAItems.getFluidPortables()) {
            portableModel(portable, "fluid", AppEng.makeId("item/portable_cell_fluid_housing"), output);
        }

        driveCell("mega_item_cell", output);
        driveCell("mega_fluid_cell", output);
        driveCell("bulk_item_cell", output);

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            flatItem(AppBotItems.MEGA_MANA_CELL_HOUSING, output);
            AppBotItems.getCells().forEach(cell -> cellModel(cell, output));
            AppBotItems.getPortables().forEach(portable -> portableModel(portable, output));
            driveCell("mega_mana_cell", output);
        }

        interfaceOrProviderPart(MEGAItems.MEGA_INTERFACE, output);
        interfaceOrProviderPart(MEGAItems.MEGA_PATTERN_PROVIDER, output);
    }

    private void flatItem(ItemDefinition<?> item, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(item.asItem()), TextureMapping.layer0(item.asItem()), output);
    }

    private void cellModel(ItemDefinition<?> cell, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        ModelTemplates.TWO_LAYERED_ITEM.create(
                ModelLocationUtils.getModelLocation(cell.asItem()),
                TextureMapping.layered(
                        MEGACells.makeId("item/cell/standard/" + cell.id().getPath()),
                        AppEng.makeId("item/storage_cell_led")),
                output);
    }

    private void portableModel(ItemDefinition<?> portable, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        ModelTemplates.TWO_LAYERED_ITEM.create(
                ModelLocationUtils.getModelLocation(portable.asItem()),
                TextureMapping.layered(
                        MEGACells.makeId("item/cell/portable/" + portable.id().getPath()),
                        AppEng.makeId("item/portable_cell_led")),
                output);
    }

    private void portableModel(
            ItemDefinition<?> portable,
            String screenType,
            ResourceLocation housingTexture,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var path = portable.id().getPath();
        var tierSuffix = path.substring(path.lastIndexOf('_') + 1);
        PORTABLE.create(
                MEGACells.makeId("item/" + portable.id().getPath()),
                new TextureMapping()
                        .put(
                                TextureSlot.LAYER0,
                                MEGACells.makeId("item/cell/portable/portable_cell_" + screenType + "_screen"))
                        .put(TextureSlot.LAYER1, AppEng.makeId("item/portable_cell_led"))
                        .put(TextureSlot.LAYER2, housingTexture)
                        .put(LAYER3, MEGACells.makeId("item/cell/portable/portable_cell_side_" + tierSuffix)),
                output);
    }

    private void driveCell(String texture, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var path = MEGACells.makeId("block/drive/cells/" + texture);
        DRIVE_CELL.create(path, new TextureMapping().put(CELL, path), output);
    }

    private void interfaceOrProviderPart(
            ItemDefinition<?> part, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var partName = part.id().getPath().substring(6);
        var front = MEGACells.makeId("part/" + partName);
        var back = MEGACells.makeId("part/" + partName + "_back");
        var sides = MEGACells.makeId("part/" + partName + "_sides");
        INTERFACE.create(
                MEGACells.makeId("part/" + partName),
                new TextureMapping()
                        .put(SIDES_STATUS, MEGACells.makeId("part/mega_monitor_sides_status"))
                        .put(SIDES, sides)
                        .put(TextureSlot.BACK, back)
                        .put(TextureSlot.FRONT, front)
                        .put(TextureSlot.PARTICLE, back),
                output);
        CABLE_INTERFACE.create(
                MEGACells.makeId("item/" + part.id().getPath()),
                new TextureMapping()
                        .put(SIDES, sides)
                        .put(TextureSlot.FRONT, front)
                        .put(TextureSlot.BACK, back),
                output);
    }

    private void simpleBlock(
            BlockDefinition<?> block,
            Consumer<BlockStateGenerator> blockStateOutput,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                block.block(), TexturedModel.CUBE.create(block.block(), modelOutput)));
        modelOutput.accept(
                ModelLocationUtils.getModelLocation(Item.BY_BLOCK.get(block.block())),
                new DelegatedModel(ModelLocationUtils.getModelLocation(block.block())));
    }

    private void energyCell(
            Consumer<BlockStateGenerator> blockStateOutput,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        var cell = MEGABlocks.MEGA_ENERGY_CELL;
        var fillStage = PropertyDispatch.property(EnergyCellBlock.ENERGY_STORAGE);
        var overrides = new HashMap<ResourceLocation, JsonObject>();

        for (var i = 0; i < 5; i++) {
            var suffix = "_" + i;
            var model = ModelTemplates.CUBE_ALL.createWithSuffix(
                    cell.block(),
                    suffix,
                    TextureMapping.cube(TextureMapping.getBlockTexture(cell.block(), suffix)),
                    modelOutput);

            fillStage.select(i, Variant.variant().with(VariantProperties.MODEL, model));

            if (i < 4) {
                var fillPredicate = new JsonObject();
                fillPredicate.addProperty("ae2:fill_level", 0.25 * i);
                overrides.put(MEGACells.makeId("block/" + cell.id().getPath() + "_" + (i + 1)), fillPredicate);
            }
        }

        blockStateOutput.accept(MultiVariantGenerator.multiVariant(cell.block()).with(fillStage));
        modelOutput.accept(
                ModelLocationUtils.getModelLocation(cell.asItem()),
                new OverrideableDelegatedModel(
                        MEGACells.makeId("block/" + cell.id().getPath() + "_0"), overrides));
    }

    private void craftingUnit(
            BlockDefinition<?> unit,
            String texture,
            Consumer<BlockStateGenerator> blockStateOutput,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        var formed = MEGACells.makeId("block/crafting/" + texture + "_formed");
        var unformed = MEGACells.makeId("block/crafting/" + texture);

        var craftingUnit = MultiVariantGenerator.multiVariant(unit.block())
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(
                                false,
                                Variant.variant()
                                        .with(
                                                VariantProperties.MODEL,
                                                ModelTemplates.CUBE_ALL.create(
                                                        unformed, TextureMapping.cube(unformed), modelOutput)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, formed)));

        blockStateOutput.accept(craftingUnit);
        modelOutput.accept(formed, () -> customModelLoader(formed));
        modelOutput.accept(ModelLocationUtils.getModelLocation(unit.asItem()), new DelegatedModel(unformed));
    }

    private void craftingMonitor(
            Consumer<BlockStateGenerator> blockStateOutput,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        var unformed = MEGACells.makeId("block/crafting/monitor");
        var unit = MEGACells.makeId("block/crafting/unit");
        var unformedModel = ModelTemplates.CUBE.create(
                unformed,
                new TextureMapping()
                        .put(TextureSlot.NORTH, unformed)
                        .put(TextureSlot.EAST, unit)
                        .put(TextureSlot.SOUTH, unit)
                        .put(TextureSlot.WEST, unit)
                        .put(TextureSlot.DOWN, unit)
                        .put(TextureSlot.UP, unit)
                        .put(TextureSlot.PARTICLE, unformed),
                modelOutput);

        var formedModel = MEGACells.makeId("block/crafting/monitor_formed");
        blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.CRAFTING_MONITOR.block())
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
        modelOutput.accept(formedModel, () -> customModelLoader(formedModel));
        modelOutput.accept(
                ModelLocationUtils.getModelLocation(MEGABlocks.CRAFTING_MONITOR.asItem()),
                new DelegatedModel(unformed));
    }

    private void patternProviderBlock(
            Consumer<BlockStateGenerator> blockStateOutput,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        var normal = ModelTemplates.CUBE_ALL.create(
                MEGABlocks.MEGA_PATTERN_PROVIDER.block(),
                TextureMapping.cube(MEGABlocks.MEGA_PATTERN_PROVIDER.block()),
                modelOutput);
        var oriented = ModelTemplates.CUBE.create(
                MEGACells.makeId("block/mega_pattern_provider_oriented"),
                new TextureMapping()
                        .put(TextureSlot.UP, MEGACells.makeId("block/mega_pattern_provider_alternate_front"))
                        .put(TextureSlot.DOWN, MEGACells.makeId("block/mega_pattern_provider_alternate"))
                        .put(TextureSlot.NORTH, MEGACells.makeId("block/mega_pattern_provider_alternate_arrow"))
                        .copySlot(TextureSlot.NORTH, TextureSlot.EAST)
                        .copySlot(TextureSlot.NORTH, TextureSlot.SOUTH)
                        .copySlot(TextureSlot.NORTH, TextureSlot.WEST)
                        .put(TextureSlot.PARTICLE, normal),
                modelOutput);

        blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.MEGA_PATTERN_PROVIDER.block())
                .with(PropertyDispatch.property(PatternProviderBlock.PUSH_DIRECTION)
                        .generate(pushDirection -> {
                            var forward = pushDirection.getDirection();

                            if (forward == null) {
                                return Variant.variant().with(VariantProperties.MODEL, normal);
                            } else {
                                var orientation = BlockOrientation.get(forward);
                                return applyRotation(
                                        Variant.variant().with(VariantProperties.MODEL, oriented),
                                        // + 90 because the default model is oriented UP, while block orientation
                                        // assumes NORTH
                                        orientation.getAngleX() + 90,
                                        orientation.getAngleY(),
                                        0);
                            }
                        })));

        modelOutput.accept(
                ModelLocationUtils.getModelLocation(MEGABlocks.MEGA_PATTERN_PROVIDER.asItem()),
                new DelegatedModel(normal));
    }

    private Variant applyOrientation(Variant variant, BlockOrientation orientation) {
        return applyRotation(variant, orientation.getAngleX(), orientation.getAngleY(), orientation.getAngleZ());
    }

    private Variant applyRotation(Variant variant, int angleX, int angleY, int angleZ) {
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

    private JsonObject customModelLoader(ResourceLocation loc) {
        var json = new JsonObject();

        if (MEGACells.PLATFORM.getLoader() == Loaders.FABRIC) {
            json.addProperty("loader", loc.toString());
        }

        return json;
    }

    @NotNull
    @Override
    public String getName() {
        return "Common Models";
    }

    private static class OverrideableDelegatedModel extends DelegatedModel {
        private final Map<ResourceLocation, JsonObject> overrides;

        public OverrideableDelegatedModel(ResourceLocation id, Map<ResourceLocation, JsonObject> overrides) {
            super(id);
            this.overrides = overrides;
        }

        @NotNull
        @Override
        public JsonElement get() {
            var json = super.get().getAsJsonObject();
            var array = new JsonArray();

            for (var override : overrides.entrySet()) {
                var entry = new JsonObject();
                entry.addProperty("model", override.getKey().toString());
                entry.add("predicate", override.getValue());
                array.add(entry);
            }

            json.add("overrides", array);
            return json;
        }
    }
}

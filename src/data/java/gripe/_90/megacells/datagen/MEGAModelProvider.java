package gripe._90.megacells.datagen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Quadrant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.client.api.model.parts.StaticPartModel;
import appeng.client.item.EnergyFillLevelProperty;
import appeng.client.item.PortableCellColorTintSource;
import appeng.client.item.StorageCellStateTintSource;
import appeng.client.model.StatusIndicatorPartModel;
import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.AE2ModelProvider;
import appeng.datagen.providers.models.PartModelOutput;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;

public class MEGAModelProvider extends ModelProvider {
    private BlockModelGenerators blockModels;
    private ItemModelGenerators itemModels;

    private final PackOutput.PathProvider partModelOutput;

    @Nullable
    private PartModelOutput partModels;

    public MEGAModelProvider(PackOutput output) {
        super(output, MEGACells.MODID);
        this.partModelOutput = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "ae2/parts");
    }

    // AE2's own AE2ModelProvider already covers AE2's namespace; MEGA only ever generated
    // for its own explicit set of blocks/items, not everything under its namespace, so opt out
    // of the strict coverage validation the base class would otherwise enforce.
    @NotNull
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @NotNull
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        this.blockModels = blockModels;
        this.itemModels = itemModels;

        basicItem(MEGAItems.SKY_STEEL_INGOT);
        basicItem(MEGAItems.SKY_BRONZE_INGOT);
        basicItem(MEGAItems.SKY_OSMIUM_INGOT);

        basicItem(MEGAItems.ACCUMULATION_PROCESSOR);
        basicItem(MEGAItems.ACCUMULATION_PROCESSOR_PRINT);
        basicItem(MEGAItems.ACCUMULATION_PROCESSOR_PRESS);

        basicItem(MEGAItems.MEGA_ITEM_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_FLUID_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_CHEMICAL_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_MANA_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_SOURCE_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_EXPERIENCE_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_SOUL_CELL_HOUSING);

        basicItem(MEGAItems.CELL_COMPONENT_1M);
        basicItem(MEGAItems.CELL_COMPONENT_4M);
        basicItem(MEGAItems.CELL_COMPONENT_16M);
        basicItem(MEGAItems.CELL_COMPONENT_64M);
        basicItem(MEGAItems.CELL_COMPONENT_256M);
        basicItem(MEGAItems.BULK_CELL_COMPONENT);
        basicItem(MEGAItems.RADIOACTIVE_CELL_COMPONENT);

        basicItem(MEGAItems.GREATER_ENERGY_CARD);
        basicItem(MEGAItems.COMPRESSION_CARD);
        basicItem(MEGAItems.PORTABLE_CELL_WORKBENCH);

        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.portable()) {
                portable(cell.item(), cell.keyType());
            } else {
                cell(cell.item(), cell.keyType());
            }
        }

        cell(MEGAItems.BULK_ITEM_CELL);
        cell(MEGAItems.RADIOACTIVE_CHEMICAL_CELL);

        MEGAItems.getTieredCells().forEach(this::driveCell);
        driveCell(MEGAItems.BULK_ITEM_CELL, 0);
        driveCell(MEGAItems.RADIOACTIVE_CHEMICAL_CELL, 2);

        simpleCube(MEGABlocks.SKY_STEEL_BLOCK.block());
        simpleCube(MEGABlocks.SKY_BRONZE_BLOCK.block());
        simpleCube(MEGABlocks.SKY_OSMIUM_BLOCK.block());
        simpleCube(MEGABlocks.MEGA_INTERFACE.block());
        simpleCube(MEGABlocks.MEGA_EMC_INTERFACE.block());

        interfaceOrProviderPart(MEGAItems.MEGA_INTERFACE);
        interfaceOrProviderPart(MEGAItems.MEGA_PATTERN_PROVIDER);
        // interfaceOrProviderPart(MEGAItems.MEGA_EMC_INTERFACE);

        craftingUnits();
        craftingMonitor();
        energyCell();
        patternProvider();

        itemModels.itemModelOutput.accept(
                MEGAItems.CELL_DOCK.asItem(),
                ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(MEGAItems.CELL_DOCK.asItem())));
        itemModels.itemModelOutput.accept(
                MEGAItems.DECOMPRESSION_MODULE.asItem(),
                ItemModelUtils.plainModel(
                        ModelLocationUtils.getModelLocation(MEGAItems.DECOMPRESSION_MODULE.asItem())));

        Objects.requireNonNull(partModels);
        partModels.staticModel(MEGAItems.CELL_DOCK, MEGAItems.CELL_DOCK.id().withPrefix("part/"));
        partModels.staticModel(
                MEGAItems.DECOMPRESSION_MODULE,
                MEGAItems.DECOMPRESSION_MODULE.id().withPrefix("part/"));
    }

    private void basicItem(ItemLike item) {
        itemModels.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private void simpleCube(Block block) {
        blockModels.createTrivialCube(block);
        blockModels.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block));
    }

    private void cell(ItemDefinition<?> cell, String housingType) {
        var id = cell.id().getPath();
        var tierSuffix = id.substring(id.lastIndexOf('_'));

        var target = ModelLocationUtils.getModelLocation(cell.asItem());
        itemModels.generateLayeredItem(
                target,
                new Material(MEGACells.makeId("item/mega_" + housingType + "_cell_housing")),
                new Material(AppEng.makeId("item/storage_cell_led")),
                new Material(MEGACells.makeId("item/storage_cell_side" + tierSuffix)));
        registerCellTint(cell.asItem(), target);
    }

    private void cell(ItemDefinition<?> cell) {
        var id = cell.id().getPath();
        var target = ModelLocationUtils.getModelLocation(cell.asItem());
        itemModels.generateLayeredItem(
                target,
                new Material(MEGACells.makeId("item/" + id)),
                new Material(AppEng.makeId("item/storage_cell_led")));
        registerCellTint(cell.asItem(), target);
    }

    private void portable(ItemDefinition<?> portable, String housingType) {
        var id = portable.id().getPath();
        var tierSuffix = id.substring(id.lastIndexOf('_'));
        var target = ModelLocationUtils.getModelLocation(portable.asItem());

        rawModel(
                target,
                "minecraft:item/generated",
                Map.of(
                        "layer0", MEGACells.makeId("item/portable_cell_" + housingType + "_housing"),
                        "layer1", AppEng.makeId("item/portable_cell_led"),
                        "layer2", AppEng.makeId("item/portable_cell_screen"),
                        "layer3", MEGACells.makeId("item/portable_cell_side" + tierSuffix)));
        blockModels.itemModelOutput.accept(
                portable.asItem(),
                ItemModelUtils.tintedModel(
                        target,
                        ItemModelUtils.constantTint(-1),
                        new StorageCellStateTintSource(),
                        new PortableCellColorTintSource()));
    }

    private void registerCellTint(Item item, Identifier target) {
        blockModels.itemModelOutput.accept(
                item,
                ItemModelUtils.tintedModel(target, ItemModelUtils.constantTint(-1), new StorageCellStateTintSource()));
    }

    private void driveCell(MEGAItems.CellDefinition cell) {
        if (cell.portable()) {
            return;
        }

        var typeOffset =
                switch (cell.keyType()) {
                    case "item" -> 0;
                    case "fluid" -> 2;
                    case "chemical" -> 4;
                    case "mana" -> 6;
                    case "source" -> 8;
                    case "experience" -> 10;
                    case "soul" -> 12;
                    default -> throw new IllegalArgumentException();
                };

        var tierOffset = (cell.tier().index() - 6) * 2;
        var name = cell.tier().namePrefix() + "_" + cell.keyType() + "_cell";

        var textures = new JsonObject();
        textures.addProperty(
                "cell", MEGACells.makeId("block/drive/cells/standard_cell").toString());
        textures.addProperty(
                "particle", MEGACells.makeId("block/drive/cells/standard_cell").toString());
        textures.addProperty(
                "tier",
                MEGACells.makeId("block/drive/cells/standard_cell_tiers").toString());

        var elements = new JsonArray();
        elements.add(driveCellElement("#cell", typeOffset));
        elements.add(driveCellElement("#tier", tierOffset));

        driveCellJson(MEGACells.makeId("block/drive/cells/" + name), textures, elements);
    }

    private void driveCell(ItemDefinition<?> cell, int offset) {
        var id = cell.id().getPath();

        var textures = new JsonObject();
        textures.addProperty(
                "cell", MEGACells.makeId("block/drive/cells/misc_cell").toString());
        textures.addProperty(
                "particle", MEGACells.makeId("block/drive/cells/misc_cell").toString());

        var elements = new JsonArray();
        elements.add(driveCellElement("#cell", offset));

        driveCellJson(MEGACells.makeId("block/drive/cells/" + id), textures, elements);
    }

    private void driveCellJson(Identifier id, JsonObject textures, JsonArray elements) {
        var json = new JsonObject();
        json.addProperty("ambientocclusion", false);
        json.add("textures", textures);
        json.add("elements", elements);
        rawJson(id, json);
    }

    private static JsonObject driveCellElement(String texture, int offset) {
        var element = new JsonObject();
        element.add("from", vec3(0, 0, 0));
        element.add("to", vec3(6, 2, 2));

        var faces = new JsonObject();
        faces.add("north", face(0, offset, 6, offset + 2, texture));
        faces.add("up", face(6, offset, 0, offset + 2, texture));
        faces.add("down", face(6, offset, 0, offset + 2, texture));
        element.add("faces", faces);

        return element;
    }

    private static JsonArray vec3(int x, int y, int z) {
        var array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private static JsonObject face(double u1, double v1, double u2, double v2, String texture) {
        var face = new JsonObject();
        var uv = new JsonArray();
        uv.add(u1);
        uv.add(v1);
        uv.add(u2);
        uv.add(v2);
        face.add("uv", uv);
        face.addProperty("texture", texture);
        face.addProperty("cullface", "north");
        return face;
    }

    private void interfaceOrProviderPart(ItemDefinition<?> part) {
        var id = part.id().getPath();
        var partName = id.substring(id.indexOf('_') + 1);
        var front = MEGACells.makeId("part/" + partName);
        var back = MEGACells.makeId("part/" + partName + "_back");
        var sides = MEGACells.makeId("part/mega_monitor_sides");

        rawModel(MEGACells.makeId("part/" + partName), "ae2:part/interface_base", new LinkedHashMap<>() {
            {
                put("sides_status", MEGACells.makeId("part/mega_monitor_sides_status"));
                put("sides", sides);
                put("front", front);
                put("back", back);
                put("particle", back);
            }
        });
        rawModel(MEGACells.makeId("item/" + id), "ae2:item/cable_interface", new LinkedHashMap<>() {
            {
                put("sides", sides);
                put("front", front);
                put("back", back);
            }
        });
        blockModels.registerSimpleItemModel(part.asItem(), MEGACells.makeId("item/" + id));
        var baseModel = AppEng.makeId("part/interface");
        Objects.requireNonNull(partModels)
                .composite(
                        part,
                        new StaticPartModel.Unbaked(MEGACells.makeId("part/" + partName)),
                        new StatusIndicatorPartModel.Unbaked(
                                baseModel.withSuffix("_has_channel"),
                                baseModel.withSuffix("_on"),
                                baseModel.withSuffix("_off")));
    }

    private void craftingUnits() {
        for (var type : MEGACraftingUnitType.values()) {
            if (type == MEGACraftingUnitType.MONITOR) {
                continue;
            }

            var craftingBlock = type.getDefinition().block();
            var name = type.getAffix();
            var unformedModel = MEGACells.makeId("block/crafting/" + name);
            rawModel(
                    unformedModel,
                    "minecraft:block/cube_all",
                    Map.of("all", MEGACells.makeId("block/crafting/" + name)));

            var formed = new CustomBlockStateModelBuilder.Simple(new MEGACraftingUnitModelProvider.Unbaked(type));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(craftingBlock)
                    .with(PropertyDispatch.initial(AbstractCraftingUnitBlock.FORMED)
                            .select(false, plainVariant(unformedModel))
                            .select(true, MultiVariant.of(formed))));
            blockModels.registerSimpleItemModel(craftingBlock, unformedModel);
        }
    }

    private void craftingMonitor() {
        var craftingUnit = MEGACells.makeId("block/crafting/unit");
        var craftingMonitor = MEGACells.makeId("block/crafting/monitor");
        var monitorUnformed = MEGACells.makeId("block/crafting/monitor");

        rawModel(monitorUnformed, "minecraft:block/cube", new LinkedHashMap<>() {
            {
                put("north", craftingMonitor);
                put("east", craftingUnit);
                put("south", craftingUnit);
                put("west", craftingUnit);
                put("up", craftingUnit);
                put("down", craftingUnit);
                put("particle", craftingMonitor);
            }
        });

        blockModels.registerSimpleItemModel(MEGABlocks.CRAFTING_MONITOR.block(), monitorUnformed);

        var formed = new CustomBlockStateModelBuilder.Simple(
                new MEGACraftingUnitModelProvider.Unbaked(MEGACraftingUnitType.MONITOR));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(MEGABlocks.CRAFTING_MONITOR.block())
                .with(PropertyDispatch.initial(AbstractCraftingUnitBlock.FORMED, BlockStateProperties.FACING)
                        .generate((isFormed, facing) -> {
                            if (isFormed) {
                                return MultiVariant.of(formed);
                            }

                            return plainVariant(monitorUnformed).with(orient(facing));
                        })));
    }

    private void energyCell() {
        var energyCellPath = MEGABlocks.MEGA_ENERGY_CELL.id().getPath();
        var energyCellModels = new ArrayList<Identifier>();

        for (var i = 0; i < 5; i++) {
            var model = MEGACells.makeId("block/" + energyCellPath + "_" + i);
            rawModel(
                    model,
                    "minecraft:block/cube_all",
                    Map.of("all", MEGACells.makeId("block/" + energyCellPath + "_" + i)));
            energyCellModels.add(model);
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(MEGABlocks.MEGA_ENERGY_CELL.block())
                .with(PropertyDispatch.initial(EnergyCellBlock.ENERGY_STORAGE)
                        .generate(i -> plainVariant(energyCellModels.get(i)))));

        var entries = new ArrayList<net.minecraft.client.renderer.item.RangeSelectItemModel.Entry>();
        for (var i = 1; i < energyCellModels.size(); i++) {
            entries.add(ItemModelUtils.override(
                    ItemModelUtils.plainModel(energyCellModels.get(i)), i / (float) energyCellModels.size()));
        }

        blockModels.itemModelOutput.accept(
                MEGABlocks.MEGA_ENERGY_CELL.asItem(),
                ItemModelUtils.rangeSelect(
                        new EnergyFillLevelProperty(),
                        ItemModelUtils.plainModel(energyCellModels.getFirst()),
                        entries));
    }

    private void patternProvider() {
        var normal = MEGACells.makeId("block/mega_pattern_provider");
        rawModel(normal, "minecraft:block/cube_all", Map.of("all", normal));
        blockModels.registerSimpleItemModel(MEGABlocks.MEGA_PATTERN_PROVIDER.block(), normal);

        var oriented = MEGACells.makeId("block/mega_pattern_provider_oriented");
        rawModel(oriented, "minecraft:block/cube_bottom_top", new LinkedHashMap<>() {
            {
                put("top", MEGACells.makeId("block/mega_pattern_provider_alternate_front"));
                put("bottom", MEGACells.makeId("block/mega_pattern_provider_alternate"));
                put("side", MEGACells.makeId("block/mega_pattern_provider_alternate_arrow"));
            }
        });

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(MEGABlocks.MEGA_PATTERN_PROVIDER.block())
                .with(PropertyDispatch.initial(PatternProviderBlock.PUSH_DIRECTION)
                        .generate(pushDirection -> {
                            var forward = pushDirection.getDirection();
                            if (forward == null) {
                                return plainVariant(normal);
                            }

                            // + 90 on X because the default model is oriented UP, while orientation assumes NORTH
                            return switch (forward) {
                                case DOWN -> plainVariant(oriented).with(xRot(Quadrant.R180));
                                case UP -> plainVariant(oriented);
                                case NORTH -> plainVariant(oriented).with(xRot(Quadrant.R90));
                                case SOUTH ->
                                    plainVariant(oriented)
                                            .with(xRot(Quadrant.R90))
                                            .with(yRot(Quadrant.R180));
                                case EAST ->
                                    plainVariant(oriented)
                                            .with(xRot(Quadrant.R90))
                                            .with(yRot(Quadrant.R90));
                                case WEST ->
                                    plainVariant(oriented)
                                            .with(xRot(Quadrant.R90))
                                            .with(yRot(Quadrant.R270));
                            };
                        })));
    }

    private static VariantMutator xRot(Quadrant quadrant) {
        return VariantMutator.X_ROT.withValue(quadrant);
    }

    private static VariantMutator yRot(Quadrant quadrant) {
        return VariantMutator.Y_ROT.withValue(quadrant);
    }

    private static VariantMutator orient(Direction facing) {
        return switch (facing) {
            case DOWN -> xRot(Quadrant.R180);
            case UP -> xRot(Quadrant.R0);
            case NORTH -> xRot(Quadrant.R90);
            case SOUTH -> xRot(Quadrant.R90).then(yRot(Quadrant.R180));
            case EAST -> xRot(Quadrant.R90).then(yRot(Quadrant.R90));
            case WEST -> xRot(Quadrant.R90).then(yRot(Quadrant.R270));
        };
    }

    private static MultiVariant plainVariant(Identifier model) {
        return BlockModelGenerators.plainVariant(model);
    }

    private void rawModel(Identifier id, String parent, Map<String, Identifier> textures) {
        var json = new JsonObject();
        if (parent != null) {
            json.addProperty("parent", parent);
        }

        var texturesJson = new JsonObject();
        textures.forEach((slot, texture) -> texturesJson.addProperty(slot, texture.toString()));
        json.add("textures", texturesJson);

        rawJson(id, json);
    }

    private void rawJson(Identifier id, JsonObject json) {
        blockModels.modelOutput.accept(id, () -> json);
    }

    @NotNull
    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput output) {
        var partModels = new AE2ModelProvider.PartModelCollector(this::getKnownItems);
        this.partModels = partModels;
        CompletableFuture<?> future;

        try {
            future = super.run(output);
        } finally {
            this.partModels = null;
        }

        partModels.finalizeAndValidate();
        return CompletableFuture.allOf(future, partModels.save(output, partModelOutput));
    }

    @NotNull
    @Override
    public String getName() {
        return "Block States / Models";
    }
}

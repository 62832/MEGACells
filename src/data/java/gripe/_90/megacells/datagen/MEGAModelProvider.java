package gripe._90.megacells.datagen;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.AE2BlockStateProvider;
import appeng.init.client.InitItemModelsProperties;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;

public class MEGAModelProvider extends AE2BlockStateProvider {
    public MEGAModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, MEGACells.MODID, existing);
    }

    @Override
    protected void registerStatesAndModels() {
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

        simpleBlockWithItem(MEGABlocks.SKY_STEEL_BLOCK.block(), cubeAll(MEGABlocks.SKY_STEEL_BLOCK.block()));
        simpleBlockWithItem(MEGABlocks.SKY_BRONZE_BLOCK.block(), cubeAll(MEGABlocks.SKY_BRONZE_BLOCK.block()));
        simpleBlockWithItem(MEGABlocks.SKY_OSMIUM_BLOCK.block(), cubeAll(MEGABlocks.SKY_OSMIUM_BLOCK.block()));
        simpleBlockWithItem(MEGABlocks.MEGA_INTERFACE.block(), cubeAll(MEGABlocks.MEGA_INTERFACE.block()));
        simpleBlockWithItem(MEGABlocks.MEGA_EMC_INTERFACE.block(), cubeAll(MEGABlocks.MEGA_EMC_INTERFACE.block()));

        interfaceOrProviderPart(MEGAItems.MEGA_INTERFACE);
        interfaceOrProviderPart(MEGAItems.MEGA_PATTERN_PROVIDER);
        interfaceOrProviderPart(MEGAItems.MEGA_EMC_INTERFACE);

        // CRAFTING UNITS
        for (var type : MEGACraftingUnitType.values()) {
            if (type == MEGACraftingUnitType.MONITOR) continue;

            var craftingBlock = type.getDefinition().block();
            var name = type.getAffix();
            var blockModel = models().cubeAll("block/crafting/" + name, MEGACells.makeId("block/crafting/" + name));
            getVariantBuilder(craftingBlock)
                    .partialState()
                    .with(AbstractCraftingUnitBlock.FORMED, false)
                    .setModels(new ConfiguredModel(blockModel))
                    .partialState()
                    .with(AbstractCraftingUnitBlock.FORMED, true)
                    .setModels(new ConfiguredModel(models().getBuilder("block/crafting/" + name + "_formed")));
            simpleBlockItem(craftingBlock, blockModel);
        }

        // CRAFTING MONITOR
        var craftingUnit = MEGACells.makeId("block/crafting/unit");
        var craftingMonitor = MEGACells.makeId("block/crafting/monitor");
        var monitorUnformed = models().cube(
                        "block/crafting/monitor",
                        craftingUnit,
                        craftingUnit,
                        craftingMonitor,
                        craftingUnit,
                        craftingUnit,
                        craftingUnit)
                .texture("particle", craftingMonitor);
        simpleBlockItem(MEGABlocks.CRAFTING_MONITOR.block(), monitorUnformed);
        multiVariantGenerator(MEGABlocks.CRAFTING_MONITOR)
                .with(PropertyDispatch.properties(AbstractCraftingUnitBlock.FORMED, BlockStateProperties.FACING)
                        .generate((formed, facing) -> {
                            if (formed) {
                                return Variant.variant()
                                        .with(
                                                VariantProperties.MODEL,
                                                models().getBuilder("block/crafting/monitor_formed")
                                                        .getLocation());
                            } else {
                                return applyOrientation(
                                        Variant.variant().with(VariantProperties.MODEL, monitorUnformed.getLocation()),
                                        BlockOrientation.get(facing));
                            }
                        }));

        // ENERGY CELL
        var energyCellPath = MEGABlocks.MEGA_ENERGY_CELL.id().getPath();
        var energyCellModels = new ArrayList<ModelFile>();

        for (var i = 0; i < 5; i++) {
            var model =
                    models().cubeAll(energyCellPath + "_" + i, MEGACells.makeId("block/" + energyCellPath + "_" + i));
            getVariantBuilder(MEGABlocks.MEGA_ENERGY_CELL.block())
                    .partialState()
                    .with(EnergyCellBlock.ENERGY_STORAGE, i)
                    .setModels(new ConfiguredModel(model));
            energyCellModels.add(model);
        }

        for (var i = 1; i < energyCellModels.size(); i++) {
            // The predicate matches "greater than", meaning for fill-level > 0 the first non-empty texture is used
            itemModels()
                    .withExistingParent(
                            energyCellPath, energyCellModels.getFirst().getLocation())
                    .override()
                    .predicate(InitItemModelsProperties.ENERGY_FILL_LEVEL_ID, i / (float) energyCellModels.size())
                    .model(energyCellModels.get(i));
        }

        // PATTERN PROVIDER
        var patternProviderNormal = cubeAll(MEGABlocks.MEGA_PATTERN_PROVIDER.block());
        simpleBlockItem(MEGABlocks.MEGA_PATTERN_PROVIDER.block(), patternProviderNormal);

        var patternProviderOriented = models().cubeBottomTop(
                        "block/mega_pattern_provider_oriented",
                        MEGACells.makeId("block/mega_pattern_provider_alternate_arrow"),
                        MEGACells.makeId("block/mega_pattern_provider_alternate"),
                        MEGACells.makeId("block/mega_pattern_provider_alternate_front"));
        multiVariantGenerator(MEGABlocks.MEGA_PATTERN_PROVIDER, Variant.variant())
                .with(PropertyDispatch.property(PatternProviderBlock.PUSH_DIRECTION)
                        .generate(pushDirection -> {
                            var forward = pushDirection.getDirection();
                            if (forward == null) {
                                return Variant.variant()
                                        .with(VariantProperties.MODEL, patternProviderNormal.getLocation());
                            } else {
                                var orientation = BlockOrientation.get(forward);
                                return applyRotation(
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, patternProviderOriented.getLocation()),
                                        // + 90 because the default model is oriented UP, while block orientation
                                        // assumes NORTH
                                        orientation.getAngleX() + 90,
                                        orientation.getAngleY(),
                                        0);
                            }
                        }));
    }

    private void basicItem(ItemLike item) {
        itemModels().basicItem(item.asItem());
    }

    private void cell(ItemDefinition<?> cell, String housingType) {
        var id = cell.id().getPath();
        var tierSuffix = id.substring(id.lastIndexOf('_'));

        itemModels()
                .singleTexture(
                        id,
                        mcLoc("item/generated"),
                        "layer0",
                        MEGACells.makeId("item/mega_" + housingType + "_cell_housing"))
                .texture("layer1", AppEng.makeId("item/storage_cell_led"))
                .texture("layer2", MEGACells.makeId("item/storage_cell_side" + tierSuffix));
    }

    private void cell(ItemDefinition<?> cell) {
        var id = cell.id().getPath();
        itemModels()
                .singleTexture(id, mcLoc("item/generated"), "layer0", MEGACells.makeId("item/" + id))
                .texture("layer1", AppEng.makeId("item/storage_cell_led"));
    }

    private void portable(ItemDefinition<?> portable, String housingType) {
        var id = portable.id().getPath();
        var tierSuffix = id.substring(id.lastIndexOf('_'));

        itemModels()
                .singleTexture(
                        id,
                        mcLoc("item/generated"),
                        "layer0",
                        MEGACells.makeId("item/portable_cell_" + housingType + "_housing"))
                .texture("layer1", AppEng.makeId("item/portable_cell_led"))
                .texture("layer2", AppEng.makeId("item/portable_cell_screen"))
                .texture("layer3", MEGACells.makeId("item/portable_cell_side" + tierSuffix));
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
                    default -> throw new IllegalArgumentException();
                };

        var tierOffset = (cell.tier().index() - 6) * 2;
        driveCell(cell.tier().namePrefix() + "_" + cell.keyType() + "_cell", "standard_cell", typeOffset)
                .texture("tier", "block/drive/cells/standard_cell_tiers")
                .element()
                .to(6, 2, 2)
                .face(Direction.NORTH)
                .uvs(0, tierOffset, 6, tierOffset + 2)
                .end()
                .face(Direction.UP)
                .uvs(6, tierOffset, 0, tierOffset + 2)
                .end()
                .face(Direction.DOWN)
                .uvs(6, tierOffset, 0, tierOffset + 2)
                .end()
                .faces((dir, builder) ->
                        builder.texture("#tier").cullface(Direction.NORTH).end())
                .end();
    }

    private void driveCell(ItemDefinition<?> cell, int offset) {
        driveCell(cell.id().getPath(), "misc_cell", offset);
    }

    private BlockModelBuilder driveCell(String cell, String texture, int offset) {
        var texturePrefix = "block/drive/cells/";
        return models().getBuilder(texturePrefix + cell)
                .ao(false)
                .texture("cell", texturePrefix + texture)
                .texture("particle", texturePrefix + texture)
                .element()
                .to(6, 2, 2)
                .face(Direction.NORTH)
                .uvs(0, offset, 6, offset + 2)
                .end()
                .face(Direction.UP)
                .uvs(6, offset, 0, offset + 2)
                .end()
                .face(Direction.DOWN)
                .uvs(6, offset, 0, offset + 2)
                .end()
                .faces((dir, builder) ->
                        builder.texture("#cell").cullface(Direction.NORTH).end())
                .end();
    }

    private void interfaceOrProviderPart(ItemDefinition<?> part) {
        var id = part.id().getPath();
        var partName = id.substring(id.indexOf('_') + 1);
        var front = MEGACells.makeId("part/" + partName);
        var back = MEGACells.makeId("part/" + partName + "_back");
        var sides = MEGACells.makeId("part/mega_monitor_sides");

        models().singleTexture(
                        "part/" + partName,
                        AppEng.makeId("part/interface_base"),
                        "sides_status",
                        MEGACells.makeId("part/mega_monitor_sides_status"))
                .texture("sides", sides)
                .texture("front", front)
                .texture("back", back)
                .texture("particle", back);
        itemModels()
                .singleTexture("item/" + id, AppEng.makeId("item/cable_interface"), "sides", sides)
                .texture("front", front)
                .texture("back", back);
    }

    @NotNull
    @Override
    public String getName() {
        return "Block States / Models";
    }
}

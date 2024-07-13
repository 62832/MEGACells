package gripe._90.megacells.datagen;

import java.util.ArrayList;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.AE2BlockStateProvider;
import appeng.init.client.InitItemModelsProperties;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;

public class MEGAModelProvider extends AE2BlockStateProvider {
    private static final ExistingFileHelper.ResourceType MODEL =
            new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");

    private final ExistingFileHelper existing;

    public MEGAModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, MEGACells.MODID, existing);
        this.existing = existing;
    }

    @Override
    protected void registerStatesAndModels() {
        basicItem(MEGAItems.SKY_STEEL_INGOT);

        basicItem(MEGAItems.ACCUMULATION_PROCESSOR);
        basicItem(MEGAItems.ACCUMULATION_PROCESSOR_PRINT);
        basicItem(MEGAItems.ACCUMULATION_PROCESSOR_PRESS);

        basicItem(MEGAItems.MEGA_ITEM_CELL_HOUSING);
        basicItem(MEGAItems.MEGA_FLUID_CELL_HOUSING);

        basicItem(MEGAItems.CELL_COMPONENT_1M);
        basicItem(MEGAItems.CELL_COMPONENT_4M);
        basicItem(MEGAItems.CELL_COMPONENT_16M);
        basicItem(MEGAItems.CELL_COMPONENT_64M);
        basicItem(MEGAItems.CELL_COMPONENT_256M);
        basicItem(MEGAItems.BULK_CELL_COMPONENT);

        basicItem(MEGAItems.GREATER_ENERGY_CARD);
        basicItem(MEGAItems.COMPRESSION_CARD);

        existing.trackGenerated(AppEng.makeId("item/storage_cell_led"), ModelProvider.TEXTURE);
        MEGAItems.getItemCells().forEach(this::cell);
        MEGAItems.getFluidCells().forEach(this::cell);
        cell(MEGAItems.BULK_ITEM_CELL);

        var itemPortableHousing = MEGACells.makeId("item/cell/portable/portable_cell_item_housing");
        var fluidPortableHousing = MEGACells.makeId("item/cell/portable/portable_cell_fluid_housing");
        existing.trackGenerated(itemPortableHousing, ModelProvider.TEXTURE);
        existing.trackGenerated(fluidPortableHousing, ModelProvider.TEXTURE);
        existing.trackGenerated(AppEng.makeId("item/portable_cell_led"), ModelProvider.TEXTURE);
        MEGAItems.getItemPortables().forEach(cell -> portable(cell, itemPortableHousing));
        MEGAItems.getFluidPortables().forEach(cell -> portable(cell, fluidPortableHousing));

        var craftingPattern =
                AppEng.makeId("item/" + AEItems.CRAFTING_PATTERN.id().getPath());
        existing.trackGenerated(craftingPattern, ModelProvider.TEXTURE);
        itemModels()
                .singleTexture(
                        MEGAItems.DECOMPRESSION_PATTERN.id().getPath(),
                        mcLoc("item/generated"),
                        "layer0",
                        craftingPattern);

        simpleBlockWithItem(MEGABlocks.SKY_STEEL_BLOCK.block(), cubeAll(MEGABlocks.SKY_STEEL_BLOCK.block()));
        simpleBlockWithItem(MEGABlocks.MEGA_INTERFACE.block(), cubeAll(MEGABlocks.MEGA_INTERFACE.block()));

        interfaceOrProviderPart(MEGAItems.MEGA_INTERFACE);
        interfaceOrProviderPart(MEGAItems.MEGA_PATTERN_PROVIDER);

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

        /*
        if (Addons.APPMEK.isLoaded()) {
            basicItem(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);

            AppMekItems.getCells().forEach(this::cell);

            var chemicalPortableHousing = MEGACells.makeId("item/cell/portable/portable_cell_chemical_housing");
            existing.trackGenerated(chemicalPortableHousing, ModelProvider.TEXTURE);
            AppMekItems.getPortables().forEach(cell -> portable(cell, chemicalPortableHousing));

            basicItem(AppMekItems.RADIOACTIVE_CELL_COMPONENT);
            cell(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);

            driveCell("mega_chemical_cell");
            driveCell("radioactive_chemical_cell");
        }
         */
    }

    private void basicItem(ItemDefinition<?> item) {
        itemModels().basicItem(item.asItem());
    }

    private void cell(ItemDefinition<?> cell) {
        var id = cell.id().getPath();
        itemModels()
                .singleTexture(id, mcLoc("item/generated"), "layer0", MEGACells.makeId("item/cell/standard/" + id))
                .texture("layer1", AppEng.makeId("item/storage_cell_led"));
    }

    private void portable(ItemDefinition<?> portable, ResourceLocation housing) {
        var id = portable.id().getPath();
        var tierSuffix = id.substring(id.lastIndexOf('_'));
        existing.trackGenerated(housing, ModelProvider.TEXTURE);

        itemModels()
                .singleTexture(id, mcLoc("item/generated"), "layer0", housing)
                .texture("layer1", AppEng.makeId("item/portable_cell_led"))
                .texture("layer2", MEGACells.makeId("item/cell/portable/portable_cell_screen"))
                .texture("layer3", MEGACells.makeId("item/cell/portable/portable_cell_side" + tierSuffix));
    }

    /*
    private void portable(ItemDefinition<?> portable) {
        var id = portable.id().getPath();
        itemModels()
                .singleTexture(id, mcLoc("item/generated"), "layer0", MEGACells.makeId("item/cell/portable/" + id))
                .texture("layer1", AppEng.makeId("item/portable_cell_led"));
    }
     */

    private void interfaceOrProviderPart(ItemDefinition<?> part) {
        var id = part.id().getPath();
        var partName = id.substring(id.indexOf('_') + 1);
        var front = MEGACells.makeId("part/" + partName);
        var back = MEGACells.makeId("part/" + partName + "_back");
        var sides = MEGACells.makeId("part/" + partName + "_sides");

        existing.trackGenerated(AppEng.makeId("part/interface_base"), MODEL);
        existing.trackGenerated(AppEng.makeId("item/cable_interface"), MODEL);

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

    @Override
    public String getName() {
        return "Block States / Models";
    }
}

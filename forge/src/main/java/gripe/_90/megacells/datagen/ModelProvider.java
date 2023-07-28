package gripe._90.megacells.datagen;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.impl.Pair;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.AE2BlockStateProvider;
import appeng.init.client.InitItemModelsProperties;

import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.util.Utils;

abstract class ModelProvider {
    static class Items extends ItemModelProvider {
        // spotless:off
        private static final ResourceLocation CRAFTING_PATTERN = AppEng.makeId("item/crafting_pattern");

        private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
        private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

        private static final ResourceLocation PORTABLE_CELL_ITEM_HOUSING = AppEng.makeId("item/portable_cell_item_housing");
        private static final ResourceLocation PORTABLE_CELL_FLUID_HOUSING = AppEng.makeId("item/portable_cell_fluid_housing");

        private static final ResourceLocation CABLE_INTERFACE = AppEng.makeId("item/cable_interface");
        //spotless:on

        public Items(PackOutput output, ExistingFileHelper existing) {
            super(output, Utils.MODID, existing);
            existing.trackGenerated(CRAFTING_PATTERN, TEXTURE);
            existing.trackGenerated(STORAGE_CELL_LED, TEXTURE);
            existing.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
            existing.trackGenerated(PORTABLE_CELL_ITEM_HOUSING, TEXTURE);
            existing.trackGenerated(PORTABLE_CELL_FLUID_HOUSING, TEXTURE);
            existing.trackGenerated(CABLE_INTERFACE, MODEL);
        }

        @Override
        protected void registerModels() {
            basicItem(MEGAItems.SKY_STEEL_INGOT.asItem());

            basicItem(MEGAItems.ACCUMULATION_PROCESSOR_PRESS.asItem());
            basicItem(MEGAItems.ACCUMULATION_PROCESSOR_PRINT.asItem());
            basicItem(MEGAItems.ACCUMULATION_PROCESSOR.asItem());

            basicItem(MEGAItems.MEGA_ITEM_CELL_HOUSING.asItem());
            basicItem(MEGAItems.MEGA_FLUID_CELL_HOUSING.asItem());

            basicItem(MEGAItems.CELL_COMPONENT_1M.asItem());
            basicItem(MEGAItems.CELL_COMPONENT_4M.asItem());
            basicItem(MEGAItems.CELL_COMPONENT_16M.asItem());
            basicItem(MEGAItems.CELL_COMPONENT_64M.asItem());
            basicItem(MEGAItems.CELL_COMPONENT_256M.asItem());
            basicItem(MEGAItems.BULK_CELL_COMPONENT.asItem());

            basicItem(MEGAItems.GREATER_ENERGY_CARD.asItem());
            basicItem(MEGAItems.COMPRESSION_CARD.asItem());

            singleTexture(MEGAItems.DECOMPRESSION_PATTERN.id().getPath(), mcLoc("item/generated"), "layer0",
                    CRAFTING_PATTERN);

            MEGAItems.getItemPortables().forEach(p -> portableModel(p, "item", PORTABLE_CELL_ITEM_HOUSING));
            MEGAItems.getFluidPortables().forEach(p -> portableModel(p, "fluid", PORTABLE_CELL_FLUID_HOUSING));

            var cells = new ArrayList<>(MEGAItems.getItemCells());
            cells.addAll(MEGAItems.getFluidCells());
            cells.add(MEGAItems.BULK_ITEM_CELL);

            cells.forEach(this::cellModel);

            patternProviderPart();
        }

        private void cellModel(ItemDefinition<?> cell) {
            var path = cell.id().getPath();
            singleTexture(path, mcLoc("item/generated"), "layer0", Utils.makeId("item/cell/standard/" + path))
                    .texture("layer1", STORAGE_CELL_LED);
        }

        private void portableModel(ItemDefinition<?> portable, String screenType, ResourceLocation housingTexture) {
            var path = portable.id().getPath();
            var tierSuffix = path.substring(path.lastIndexOf('_') + 1);
            singleTexture(path, mcLoc("item/generated"), "layer0",
                    Utils.makeId("item/cell/portable/portable_cell_%s_screen".formatted(screenType)))
                            .texture("layer1", PORTABLE_CELL_LED).texture("layer2", housingTexture)
                            .texture("layer3", "item/cell/portable/portable_cell_side_%s".formatted(tierSuffix));
        }

        private void patternProviderPart() {
            withExistingParent(MEGAParts.MEGA_PATTERN_PROVIDER.id().getPath(), CABLE_INTERFACE)
                    .texture("back", "part/mega_monitor_back")
                    .texture("front", "part/mega_pattern_provider")
                    .texture("sides", "part/mega_monitor_sides");
        }
    }

    static class Blocks extends AE2BlockStateProvider {
        // because for whatever reason this isn't fucking accessible from BlockStateProvider
        private static final ExistingFileHelper.ResourceType MODEL = new ExistingFileHelper.ResourceType(
                PackType.CLIENT_RESOURCES, ".json", "models");

        private static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

        public Blocks(PackOutput output, ExistingFileHelper existing) {
            super(output, Utils.MODID, existing);
            existing.trackGenerated(DRIVE_CELL, MODEL);
        }

        @Override
        protected void registerStatesAndModels() {
            simpleBlockAndItem(MEGABlocks.SKY_STEEL_BLOCK);

            energyCell();
            patternProvider();

            var craftingUnits = List.of(
                    Pair.of(MEGABlocks.MEGA_CRAFTING_UNIT, "unit"),
                    Pair.of(MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage"),
                    Pair.of(MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage"),
                    Pair.of(MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage"),
                    Pair.of(MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage"),
                    Pair.of(MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage"),
                    Pair.of(MEGABlocks.CRAFTING_ACCELERATOR, "accelerator"));
            craftingUnits.forEach(block -> craftingModel(block.first, block.second));
            craftingMonitor();

            driveCell("mega_item_cell");
            driveCell("mega_fluid_cell");
            driveCell("bulk_item_cell");
        }

        private void driveCell(String texture) {
            var path = "block/drive/cells/" + texture;
            models().withExistingParent(path, DRIVE_CELL).texture("cell", path);
        }

        private void energyCell() {
            var cell = MEGABlocks.MEGA_ENERGY_CELL;
            var path = cell.id().getPath();
            var blockBuilder = getVariantBuilder(cell.block());
            var models = new ArrayList<ModelFile>();

            for (var i = 0; i < 5; i++) {
                var model = models().cubeAll(path + "_" + i, Utils.makeId("block/" + path + "_" + i));
                blockBuilder.partialState().with(EnergyCellBlock.ENERGY_STORAGE, i)
                        .setModels(new ConfiguredModel(model));
                models.add(model);
            }

            var item = itemModels().withExistingParent(path, models.get(0).getLocation());

            for (var i = 1; i < models.size(); i++) {
                float fillFactor = i / (float) models.size();
                item.override()
                        .predicate(InitItemModelsProperties.ENERGY_FILL_LEVEL_ID, fillFactor)
                        .model(models.get(i));
            }
        }

        private void craftingModel(BlockDefinition<?> block, String name) {
            var blockModel = models().cubeAll("block/crafting/" + name, Utils.makeId("block/crafting/" + name));
            getVariantBuilder(block.block())
                    .partialState().with(AbstractCraftingUnitBlock.FORMED, false)
                    .setModels(new ConfiguredModel(blockModel))
                    .partialState().with(AbstractCraftingUnitBlock.FORMED, true)
                    .setModels(new ConfiguredModel(models().getBuilder("ae2:block/crafting/mega_" + name + "_formed")));
            simpleBlockItem(block.block(), blockModel);
        }

        private void craftingMonitor() {
            var formedModel = AppEng.makeId("block/crafting/mega_monitor_formed");
            models().getBuilder("ae2:block/crafting/mega_monitor_formed");

            var monitor = Utils.makeId("block/crafting/monitor");
            var unit = Utils.makeId("block/crafting/unit");
            var unformedModel = models().cube("block/crafting/mega_monitor", unit, unit, monitor, unit, unit, unit)
                    .texture("particle", monitor);

            multiVariantGenerator(MEGABlocks.CRAFTING_MONITOR)
                    .with(PropertyDispatch.properties(AbstractCraftingUnitBlock.FORMED, BlockStateProperties.FACING)
                            .generate((formed, facing) -> {
                                if (formed) {
                                    return Variant.variant().with(VariantProperties.MODEL, formedModel);
                                } else {
                                    return applyOrientation(
                                            Variant.variant().with(VariantProperties.MODEL,
                                                    unformedModel.getLocation()),
                                            BlockOrientation.get(facing));
                                }
                            }));

            simpleBlockItem(MEGABlocks.CRAFTING_MONITOR.block(), unformedModel);
        }

        private void patternProvider() {
            var def = MEGABlocks.MEGA_PATTERN_PROVIDER;
            var normalModel = cubeAll(def.block());
            simpleBlockItem(def.block(), normalModel);

            var arrow = Utils.makeId("block/mega_pattern_provider_alternate_arrow");
            var orientedModel = models().cube("block/mega_pattern_provider_oriented",
                    Utils.makeId("block/mega_pattern_provider_alternate"),
                    Utils.makeId("block/mega_pattern_provider_alternate_front"), arrow, arrow, arrow, arrow)
                    .texture("particle", "block/mega_pattern_provider");

            multiVariantGenerator(MEGABlocks.MEGA_PATTERN_PROVIDER, Variant.variant())
                    .with(PropertyDispatch.property(MEGAPatternProviderBlock.PUSH_DIRECTION).generate(pushDirection -> {
                        var forward = pushDirection.getDirection();
                        if (forward == null) {
                            return Variant.variant().with(VariantProperties.MODEL, normalModel.getLocation());
                        } else {
                            var orientation = BlockOrientation.get(forward);
                            return applyRotation(
                                    Variant.variant().with(VariantProperties.MODEL, orientedModel.getLocation()),
                                    // + 90 because the default model is oriented UP, while block orientation assumes
                                    // NORTH
                                    orientation.getAngleX() + 90,
                                    orientation.getAngleY(),
                                    0);
                        }
                    }));
        }
    }

    static class Parts extends net.minecraftforge.client.model.generators.ModelProvider<BlockModelBuilder> {
        private static final ResourceLocation PATTERN_PROVIDER = AppEng.makeId("part/pattern_provider_base");

        public Parts(PackOutput output, ExistingFileHelper existing) {
            super(output, Utils.MODID, "part", BlockModelBuilder::new, existing);
            existing.trackGenerated(PATTERN_PROVIDER, MODEL);
        }

        @NotNull
        @Override
        public String getName() {
            return "Part Models: " + modid;
        }

        @Override
        protected void registerModels() {
            patternProvider();
        }

        private void patternProvider() {
            withExistingParent("part/mega_pattern_provider", PATTERN_PROVIDER)
                    .texture("back", "part/mega_monitor_back")
                    .texture("front", "part/mega_pattern_provider")
                    .texture("particle", "part/mega_monitor_back")
                    .texture("sides", "part/mega_monitor_sides")
                    .texture("sidesStatus", "part/mega_monitor_sides_status");
        }
    }
}

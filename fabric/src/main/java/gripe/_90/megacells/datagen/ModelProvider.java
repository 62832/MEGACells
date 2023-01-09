package gripe._90.megacells.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.util.Utils;

class ModelProvider extends FabricModelProvider {
    static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    static final TextureSlot CELL_TEXTURE = TextureSlot.create("cell");

    static final ModelTemplate CELL = new ModelTemplate(Optional.of(new ResourceLocation("item/generated")),
            Optional.empty(), TextureSlot.LAYER0, LAYER1);
    static final ModelTemplate DRIVE_CELL = new ModelTemplate(Optional.of(AppEng.makeId("block/drive/drive_cell")),
            Optional.empty(), CELL_TEXTURE);

    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    ModelProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        createEnergyCell(generator);

        for (var block : CommonModelSupplier.CRAFTING_UNITS) {
            createCraftingUnit(block.first.block(), block.second, generator);
        }
        createCraftingMonitor(generator);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        for (var item : CommonModelSupplier.FLAT_ITEMS) {
            generator.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
        }

        for (var cell : CommonModelSupplier.STORAGE_CELLS) {
            createCellItem(
                    Utils.makeId("item/cell/standard/" + cell.id().getPath()),
                    Utils.makeId("item/" + cell.id().getPath()), STORAGE_CELL_LED, generator.output);
        }

        for (var cell : CommonModelSupplier.PORTABLE_CELLS) {
            createCellItem(
                    Utils.makeId("item/cell/portable/" + cell.id().getPath()),
                    Utils.makeId("item/" + cell.id().getPath()), PORTABLE_CELL_LED, generator.output);
        }

        createDriveCellModel("mega_item_cell", generator.output);
        createDriveCellModel("mega_fluid_cell", generator.output);
        createDriveCellModel("mega_mana_cell", generator.output);
        createDriveCellModel("bulk_item_cell", generator.output);
    }

    private TextureMapping cell(ResourceLocation cell, ResourceLocation led) {
        return new TextureMapping().put(TextureSlot.LAYER0, cell).put(LAYER1, led);
    }

    private TextureMapping driveCell(ResourceLocation cell) {
        return new TextureMapping().put(CELL_TEXTURE, cell);
    }

    private void createCellItem(ResourceLocation cellTexture, ResourceLocation cellModel, ResourceLocation led,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        CELL.create(cellModel, cell(cellTexture, led), output);
    }

    private void createDriveCellModel(String texture,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var path = Utils.makeId("block/drive/cells/" + texture);
        DRIVE_CELL.create(path, driveCell(path), output);
    }

    private MultiVariantGenerator craftingUnit(Block block, String texture,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var formed = Utils.makeId("block/crafting/" + texture + "_formed");
        var unformed = Utils.makeId("block/crafting/" + texture);
        return MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL,
                                ModelTemplates.CUBE_ALL.create(unformed, TextureMapping.cube(unformed), output)))
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
                        .select(false, Variant.variant().with(VariantProperties.MODEL,
                                ModelTemplates.CUBE.create(unformed, new TextureMapping()
                                        .put(TextureSlot.NORTH, unformed)
                                        .put(TextureSlot.EAST, unit)
                                        .put(TextureSlot.SOUTH, unit)
                                        .put(TextureSlot.WEST, unit)
                                        .put(TextureSlot.DOWN, unit)
                                        .put(TextureSlot.UP, unit)
                                        .put(TextureSlot.PARTICLE, unformed), generator.modelOutput)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, formed))));
        generator.modelOutput.accept(formed, () -> customModelLoader(formed));
        generator.delegateItemModel(MEGABlocks.CRAFTING_MONITOR.block(), unformed);
    }

    private JsonObject customModelLoader(ResourceLocation loc) {
        var json = new JsonObject();
        json.addProperty("loader", loc.toString());
        return json;
    }

    private void createEnergyCell(BlockModelGenerators generator) {
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

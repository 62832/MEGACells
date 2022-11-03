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

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.init.client.InitItemModelsProperties;

public class ModelProvider extends FabricModelProvider {

    static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    static final TextureSlot CELL = TextureSlot.create("cell");

    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    public ModelProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        energyCell(generator);

        for (var block : CommonModelSupplier.CRAFTING_UNITS) {
            craftingBlock(block.first, block.second, generator);
        }
        craftingMonitor(generator);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        for (var item : CommonModelSupplier.FLAT_ITEMS) {
            generator.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
        }

        for (var item : CommonModelSupplier.STORAGE_CELLS) {
            cell(item, STORAGE_CELL_LED, "standard", generator.output);
            driveCell(item.id().getPath(), "standard", generator.output);
        }

        for (var item : CommonModelSupplier.PORTABLE_CELLS) {
            cell(item, PORTABLE_CELL_LED, "portable", generator.output);
        }

        driveCell("portable_mega_item_cell", "portable", generator.output);
        driveCell("portable_mega_fluid_cell", "portable", generator.output);
    }

    private void cell(ItemDefinition<?> item, ResourceLocation led, String subfolder,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var texture = MEGACells.makeId("item/cell/" + subfolder + "/" + item.id().getPath());
        var template = new ModelTemplate(Optional.of(new ResourceLocation("item/generated")),
                Optional.empty(), TextureSlot.LAYER0, LAYER1);
        var mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, texture)
                .put(LAYER1, led);

        template.create(MEGACells.makeId("item/" + item.id().getPath()), mapping, output);
    }

    private void driveCell(String texture, String subfolder,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var textureLoc = MEGACells.makeId("block/drive/cells/" + subfolder + "/" + texture);
        var template = new ModelTemplate(Optional.of(AppEng.makeId("block/drive/drive_cell")), Optional.empty(), CELL);
        var mapping = new TextureMapping().put(CELL, textureLoc);

        template.create(textureLoc, mapping, output);
    }

    private void craftingBlock(BlockDefinition<?> block, String texture, BlockModelGenerators generator) {
        var formed = MEGACells.makeId("block/crafting/" + texture + "_formed");
        var unformed = MEGACells.makeId("block/crafting/" + texture);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.block())
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CUBE_ALL.create(
                                unformed, TextureMapping.cube(unformed), generator.modelOutput)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, formed))));
        generator.modelOutput.accept(formed, () -> customModelLoader(formed));
        generator.delegateItemModel(block.block(), unformed);
    }

    private void craftingMonitor(BlockModelGenerators generator) {
        var formed = MEGACells.makeId("block/crafting/monitor_formed");
        var unformed = MEGACells.makeId("block/crafting/monitor");
        var unit = MEGACells.makeId("block/crafting/unit");

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MEGABlocks.CRAFTING_MONITOR.block())
                .with(PropertyDispatch.property(AbstractCraftingUnitBlock.FORMED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CUBE.create(
                                unformed, new TextureMapping()
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

    private void energyCell(BlockModelGenerators generator) {
        var cell = MEGABlocks.MEGA_ENERGY_CELL;
        var fillStage = PropertyDispatch.property(EnergyCellBlock.ENERGY_STORAGE);
        List<Pair<ResourceLocation, JsonObject>> itemModelOverrides = new ArrayList<>();

        for (var i = 0; i < 5; i++) {
            fillStage.select(i, Variant.variant().with(VariantProperties.MODEL, generator
                    .createSuffixedVariant(cell.block(), "_" + i, ModelTemplates.CUBE_ALL, TextureMapping::cube)));

            if (i < 4) {
                var fillPredicate = new JsonObject();
                fillPredicate.addProperty(InitItemModelsProperties.ENERGY_FILL_LEVEL_ID.toString(), 0.25 * i);
                itemModelOverrides.add(
                        Pair.of(MEGACells.makeId("block/" + cell.id().getPath() + "_" + (i + 1)), fillPredicate));
            }
        }
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cell.block()).with(fillStage));
        generator.modelOutput.accept(MEGACells.makeId("item/" + cell.id().getPath()), new OverrideableDelegatedModel(
                MEGACells.makeId("block/" + cell.id().getPath() + "_0"), itemModelOverrides));
    }

    static class OverrideableDelegatedModel extends DelegatedModel {

        private final List<Pair<ResourceLocation, JsonObject>> overrides;

        public OverrideableDelegatedModel(ResourceLocation resourceLocation,
                List<Pair<ResourceLocation, JsonObject>> overrides) {
            super(resourceLocation);
            this.overrides = overrides;
        }

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

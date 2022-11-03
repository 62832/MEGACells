package gripe._90.megacells.datagen;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.cell.MEGACellType;

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

    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        for (var item : CommonModelSupplier.FLAT_ITEMS) {
            generator.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
        }

        for (var item : CommonModelSupplier.STORAGE_CELLS) {
            cell(item, STORAGE_CELL_LED, "standard", generator.output);
            driveCell(item, generator.output);
        }

        for (var item : CommonModelSupplier.PORTABLE_CELLS) {
            cell(item, PORTABLE_CELL_LED, "portable", generator.output);
        }

        for (var item : MEGACellType.ITEM.getPortableCells()) {
            driveCell(item, "portable_mega_item_cell", "portable", generator.output);
        }

        for (var item : MEGACellType.FLUID.getPortableCells()) {
            driveCell(item, "portable_mega_fluid_cell", "portable", generator.output);
        }
    }

    private void cell(ItemDefinition<?> item, ResourceLocation led, String subfolder,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var loc = MEGACells.makeId("item/cell/" + subfolder + "/" + item.id().getPath());
        var template = new ModelTemplate(Optional.of(new ResourceLocation("item/generated")),
                Optional.empty(), TextureSlot.LAYER0, LAYER1);
        var mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, loc)
                .put(LAYER1, led);

        template.create(loc, mapping, output);
    }

    private void driveCell(ItemDefinition<?> cell, BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        driveCell(cell, "standard", cell.id().getPath(), output);
    }

    private void driveCell(ItemDefinition<?> cell, String driveCellTexture, String subfolder,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var texture = MEGACells.makeId("block/drive/cells/" + subfolder + "/" + driveCellTexture);
        var template = new ModelTemplate(Optional.of(AppEng.makeId("block/drive/drive_cell")), Optional.empty(), CELL);
        var mapping = new TextureMapping().put(CELL, texture);

        template.create(cell.id(), mapping, output);
    }
}

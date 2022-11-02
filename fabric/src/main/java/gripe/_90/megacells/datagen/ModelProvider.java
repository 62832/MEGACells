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

public class ModelProvider extends FabricModelProvider {

    static final TextureSlot LAYER1 = TextureSlot.create("layer1");

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
        }

        for (var item : CommonModelSupplier.PORTABLE_CELLS) {
            cell(item, PORTABLE_CELL_LED, "portable", generator.output);
        }
    }

    private void cell(ItemDefinition<?> item, ResourceLocation led, String subfolder,
            BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        var template = new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/generated")),
                Optional.empty(), TextureSlot.LAYER0, LAYER1);
        var mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, TextureMapping.getItemTexture(item.asItem()))
                .put(LAYER1, led);

        template.create(MEGACells.makeId("item/cell/" + subfolder + "/" + item.id().getPath()), mapping, output);
    }
}

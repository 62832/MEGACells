package gripe._90.megacells.datagen;

import java.util.List;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;

import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.item.core.MEGATier;

public class MEGAModelProvider extends FabricModelProvider {

    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    public MEGAModelProvider(FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        // TODO: FUCK FUCK FUCK FUCK FUCK
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(MEGAItems.MEGA_ITEM_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(MEGAItems.MEGA_FLUID_CELL_HOUSING.asItem(), ModelTemplates.FLAT_ITEM);

        for (var tier : MEGATier.values()) {
            generator.generateFlatItem(tier.getComponent(), ModelTemplates.FLAT_ITEM);
        }
        generator.generateFlatItem(MEGAItems.BULK_CELL_COMPONENT.asItem(), ModelTemplates.FLAT_ITEM);

        for (var storage : List.of(MEGAItems.ITEM_CELL_1M, MEGAItems.ITEM_CELL_4M, MEGAItems.ITEM_CELL_16M,
                MEGAItems.ITEM_CELL_64M, MEGAItems.ITEM_CELL_256M, MEGAItems.FLUID_CELL_1M, MEGAItems.FLUID_CELL_4M,
                MEGAItems.FLUID_CELL_16M, MEGAItems.FLUID_CELL_64M, MEGAItems.FLUID_CELL_256M,
                MEGAItems.BULK_ITEM_CELL)) {
            generateCellItem(storage, STORAGE_CELL_LED, generator);
        }

        for (var portable : List.of(MEGAItems.PORTABLE_ITEM_CELL_1M, MEGAItems.PORTABLE_ITEM_CELL_4M,
                MEGAItems.PORTABLE_ITEM_CELL_16M, MEGAItems.PORTABLE_ITEM_CELL_64M, MEGAItems.PORTABLE_ITEM_CELL_256M,
                MEGAItems.PORTABLE_FLUID_CELL_1M, MEGAItems.PORTABLE_FLUID_CELL_4M, MEGAItems.PORTABLE_FLUID_CELL_16M,
                MEGAItems.PORTABLE_FLUID_CELL_64M, MEGAItems.PORTABLE_FLUID_CELL_256M)) {
            generateCellItem(portable, PORTABLE_CELL_LED, generator);
        }
    }

    private void generateCellItem(MEGAItems.ItemDefinition<?> cell, ResourceLocation led, ItemModelGenerators gen) {
        var mapping = TextureMapping.layer0(cell.asItem()).put(TextureSlot.create("layer1"), led);
        ModelTemplates.FLAT_ITEM.create(cell.getId(), mapping, gen.output);
    }
}

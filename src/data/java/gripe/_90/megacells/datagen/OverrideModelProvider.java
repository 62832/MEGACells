package gripe._90.megacells.datagen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.AEItems;

import me.ramidzkh.mekae2.AMItems;

import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.megacells.MEGACells;

import es.degrassi.appexp.definition.AExpItems;

public class OverrideModelProvider extends ItemModelProvider {
    public OverrideModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, MEGACells.MODID, existing);
    }

    @Override
    protected void registerModels() {
        cell(AEItems.ITEM_CELL_1K, AEItems.ITEM_CELL_HOUSING);
        cell(AEItems.ITEM_CELL_4K, AEItems.ITEM_CELL_HOUSING);
        cell(AEItems.ITEM_CELL_16K, AEItems.ITEM_CELL_HOUSING);
        cell(AEItems.ITEM_CELL_64K, AEItems.ITEM_CELL_HOUSING);
        cell(AEItems.ITEM_CELL_256K, AEItems.ITEM_CELL_HOUSING);

        cell(AEItems.FLUID_CELL_1K, AEItems.FLUID_CELL_HOUSING);
        cell(AEItems.FLUID_CELL_4K, AEItems.FLUID_CELL_HOUSING);
        cell(AEItems.FLUID_CELL_16K, AEItems.FLUID_CELL_HOUSING);
        cell(AEItems.FLUID_CELL_64K, AEItems.FLUID_CELL_HOUSING);
        cell(AEItems.FLUID_CELL_256K, AEItems.FLUID_CELL_HOUSING);

        existingFileHelper.trackGenerated(textureLocation(AMItems.CHEMICAL_CELL_HOUSING), TEXTURE);
        cell(AMItems.CHEMICAL_CELL_1K, AMItems.CHEMICAL_CELL_HOUSING);
        cell(AMItems.CHEMICAL_CELL_4K, AMItems.CHEMICAL_CELL_HOUSING);
        cell(AMItems.CHEMICAL_CELL_16K, AMItems.CHEMICAL_CELL_HOUSING);
        cell(AMItems.CHEMICAL_CELL_64K, AMItems.CHEMICAL_CELL_HOUSING);
        cell(AMItems.CHEMICAL_CELL_256K, AMItems.CHEMICAL_CELL_HOUSING);

        existingFileHelper.trackGenerated(textureLocation(ArsEngItems.SOURCE_CELL_HOUSING), TEXTURE);
        cell(ArsEngItems.SOURCE_CELL_1K, ArsEngItems.SOURCE_CELL_HOUSING);
        cell(ArsEngItems.SOURCE_CELL_4K, ArsEngItems.SOURCE_CELL_HOUSING);
        cell(ArsEngItems.SOURCE_CELL_16K, ArsEngItems.SOURCE_CELL_HOUSING);
        cell(ArsEngItems.SOURCE_CELL_64K, ArsEngItems.SOURCE_CELL_HOUSING);
        cell(ArsEngItems.SOURCE_CELL_256K, ArsEngItems.SOURCE_CELL_HOUSING);

        existingFileHelper.trackGenerated(textureLocation(AExpItems.EXPERIENCE_CELL_HOUSING), TEXTURE);
        cell(AExpItems.EXPERIENCE_CELL_1K, AExpItems.EXPERIENCE_CELL_HOUSING);
        cell(AExpItems.EXPERIENCE_CELL_4K, AExpItems.EXPERIENCE_CELL_HOUSING);
        cell(AExpItems.EXPERIENCE_CELL_16K, AExpItems.EXPERIENCE_CELL_HOUSING);
        cell(AExpItems.EXPERIENCE_CELL_64K, AExpItems.EXPERIENCE_CELL_HOUSING);
        cell(AExpItems.EXPERIENCE_CELL_256K, AExpItems.EXPERIENCE_CELL_HOUSING);
    }

    private void cell(ItemLike cell, ItemLike housing) {
        var id = BuiltInRegistries.ITEM.getKey(cell.asItem());
        var tierSuffix = id.getPath().substring(id.getPath().lastIndexOf('_'));

        singleTexture(id.toString(), mcLoc("item/generated"), "layer0", textureLocation(housing))
                .texture("layer1", AppEng.makeId("item/storage_cell_led"))
                .texture("layer2", AppEng.makeId("item/storage_cell_side" + tierSuffix));
    }

    private ResourceLocation textureLocation(ItemLike item) {
        var id = BuiltInRegistries.ITEM.getKey(item.asItem());
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    @NotNull
    @Override
    public String getName() {
        return "Item Models (Classic Cell Colours)";
    }
}

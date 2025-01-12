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

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.datagen.integration.AppBotIntegrationData;
import gripe._90.megacells.datagen.integration.AppExIntegrationData;
import gripe._90.megacells.datagen.integration.AppMekIntegrationData;
import gripe._90.megacells.datagen.integration.ArsEngIntegrationData;
import gripe._90.megacells.integration.Addons;

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

        if (Addons.APPMEK.isLoaded()) {
            existingFileHelper.trackGenerated(textureLocation(AppMekIntegrationData.CHEMICAL_CELL_HOUSING), TEXTURE);
            AppMekIntegrationData.getCells().forEach(c -> cell(c, AppMekIntegrationData.CHEMICAL_CELL_HOUSING));
        }

        if (Addons.APPBOT.isLoaded()) {
            existingFileHelper.trackGenerated(textureLocation(AppBotIntegrationData.MANA_CELL_HOUSING), TEXTURE);
            AppBotIntegrationData.getCells().forEach(c -> cell(c, AppBotIntegrationData.MANA_CELL_HOUSING));
        }

        if (Addons.ARSENG.isLoaded()) {
            existingFileHelper.trackGenerated(textureLocation(ArsEngIntegrationData.SOURCE_CELL_HOUSING), TEXTURE);
            ArsEngIntegrationData.getCells().forEach(c -> cell(c, ArsEngIntegrationData.SOURCE_CELL_HOUSING));
        }

        if (Addons.APPEX.isLoaded()) {
            existingFileHelper.trackGenerated(textureLocation(AppExIntegrationData.EXPERIENCE_CELL_HOUSING), TEXTURE);
            AppExIntegrationData.getCells().forEach(c -> cell(c, AppExIntegrationData.EXPERIENCE_CELL_HOUSING));
        }
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

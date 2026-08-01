package gripe._90.megacells.datagen;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import appeng.core.AppEng;
import appeng.core.definitions.AEItems;

import gripe._90.megacells.MEGACells;

public class OverrideModelProvider extends ModelProvider {
    private ItemModelGenerators itemModels;

    public OverrideModelProvider(PackOutput output) {
        super(output, MEGACells.MODID);
    }

    // This pack only overrides AE2's own cell item models, not anything under MEGA's namespace,
    // so opt out of the strict coverage validation the base class would otherwise enforce.
    @NotNull
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @NotNull
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        this.itemModels = itemModels;

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

        // AppMek, AppBot, ArsEng, AppEx and Applied Soul don't have Minecraft 26.1 releases yet
        // (see build.gradle.kts), so there's no datagen counterpart to call into here right now.
        // Left commented out as a reference point for when their datagen is re-implemented:
        //
        // if (Addons.APPMEK.isLoaded()) {
        //     existingFileHelper.trackGenerated(textureLocation(AppMekIntegrationData.CHEMICAL_CELL_HOUSING), TEXTURE);
        //     AppMekIntegrationData.getCells().forEach(c -> cell(c, AppMekIntegrationData.CHEMICAL_CELL_HOUSING));
        // }
        //
        // if (Addons.APPBOT.isLoaded()) {
        //     existingFileHelper.trackGenerated(textureLocation(AppBotIntegrationData.MANA_CELL_HOUSING), TEXTURE);
        //     AppBotIntegrationData.getCells().forEach(c -> cell(c, AppBotIntegrationData.MANA_CELL_HOUSING));
        // }
        //
        // if (Addons.ARSENG.isLoaded()) {
        //     existingFileHelper.trackGenerated(textureLocation(ArsEngIntegrationData.SOURCE_CELL_HOUSING), TEXTURE);
        //     ArsEngIntegrationData.getCells().forEach(c -> cell(c, ArsEngIntegrationData.SOURCE_CELL_HOUSING));
        // }
        //
        // if (Addons.APPEX.isLoaded()) {
        //     existingFileHelper.trackGenerated(textureLocation(AppExIntegrationData.EXPERIENCE_CELL_HOUSING),
        // TEXTURE);
        //     AppExIntegrationData.getCells().forEach(c -> cell(c, AppExIntegrationData.EXPERIENCE_CELL_HOUSING));
        // }
        //
        // if (Addons.APPLIEDSOUL.isLoaded()) {
        //     existingFileHelper.trackGenerated(textureLocation(AppSoulIntegrationData.SOUL_CELL_HOSING), TEXTURE);
        //     AppSoulIntegrationData.getCells().forEach(c -> cell(c, AppSoulIntegrationData.SOUL_CELL_HOSING));
        // }
    }

    private void cell(ItemLike cell, ItemLike housing) {
        var id = BuiltInRegistries.ITEM.getKey(cell.asItem());
        var tierSuffix = id.getPath().substring(id.getPath().lastIndexOf('_'));
        var target = ModelLocationUtils.getModelLocation(cell.asItem());

        itemModels.generateLayeredItem(
                target,
                new Material(textureLocation(housing)),
                new Material(AppEng.makeId("item/storage_cell_led")),
                new Material(AppEng.makeId("item/storage_cell_side" + tierSuffix)));
        itemModels.itemModelOutput.accept(cell.asItem(), ItemModelUtils.plainModel(target));
    }

    private Identifier textureLocation(ItemLike item) {
        var id = BuiltInRegistries.ITEM.getKey(item.asItem());
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    @NotNull
    @Override
    public String getName() {
        return "Item Models (Classic Cell Colours)";
    }
}

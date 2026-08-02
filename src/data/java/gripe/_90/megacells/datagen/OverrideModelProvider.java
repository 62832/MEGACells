package gripe._90.megacells.datagen;

import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.Addons;

public class OverrideModelProvider extends ModelProvider {
    private ItemModelGenerators itemModels;

    public OverrideModelProvider(PackOutput output) {
        super(output, MEGACells.MODID);
    }

    // This pack only overrides AE2's and other add-ons' own cell item models, not anything under MEGA's namespace,
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
        cells("item", null);
        cells("fluid", null);

        cells("chemical", Addons.APPMEK);
        cells("mana", Addons.APPBOT);
        cells("source", Addons.ARSENG);
        cells("experience", Addons.APPEX);
        cells("soul", Addons.APPLIEDSOUL);
    }

    private void cells(String keyType, Addons addon) {
        var namespace = addon == null ? AppEng.MOD_ID : addon.getModId();

        for (var tier : List.of("1k", "4k", "16k", "64k", "256k")) {
            var cell = Identifier.fromNamespaceAndPath(namespace, keyType + "_storage_cell_" + tier);
            var target = cell.withPrefix("item/");
            itemModels.generateLayeredItem(
                    target,
                    new Material(Identifier.fromNamespaceAndPath(namespace, "item/" + keyType + "_cell_housing")),
                    new Material(AppEng.makeId("item/storage_cell_led")),
                    new Material(AppEng.makeId("item/storage_cell_side_" + tier)));
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "Item Models (Classic Cell Colours)";
    }
}

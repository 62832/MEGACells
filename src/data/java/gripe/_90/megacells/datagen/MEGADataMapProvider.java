package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import gripe._90.megacells.definition.MEGADataMaps;

public class MEGADataMapProvider extends DataMapProvider {
    protected MEGADataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void gather() {
        var overrides = builder(MEGADataMaps.COMPRESSION_OVERRIDE);
        compressionOverride(overrides, Items.QUARTZ, Items.QUARTZ_BLOCK);
        compressionOverride(overrides, Items.GLOWSTONE_DUST, Items.GLOWSTONE);
        compressionOverride(overrides, Items.AMETHYST_SHARD, Items.AMETHYST_BLOCK);
        compressionOverride(overrides, Items.MAGMA_CREAM, Items.MAGMA_BLOCK);
        compressionOverride(overrides, Items.CLAY_BALL, Items.CLAY);
        compressionOverride(overrides, Items.MELON_SLICE, Items.MELON);
        compressionOverride(overrides, Items.ICE, Items.PACKED_ICE);
        compressionOverride(overrides, Items.PACKED_ICE, Items.BLUE_ICE);
        compressionOverride(overrides, Items.STRING, Items.WHITE_WOOL);
        compressionOverride(overrides, Items.SNOWBALL, Items.SNOW_BLOCK);
        compressionOverride(overrides, Items.HONEYCOMB, Items.HONEYCOMB_BLOCK);
        compressionOverride(overrides, Items.POINTED_DRIPSTONE, Items.DRIPSTONE_BLOCK);
    }

    private static void compressionOverride(Builder<Item, Item> builder, Item base, Item variant) {
        builder.add(BuiltInRegistries.ITEM.getKey(base), variant, false);
    }
}

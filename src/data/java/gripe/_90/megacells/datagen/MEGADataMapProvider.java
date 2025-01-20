package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
        builder(MEGADataMaps.COMPRESSION_OVERRIDE)
                .add(itemId(Items.QUARTZ), Items.QUARTZ_BLOCK, false)
                .add(itemId(Items.GLOWSTONE_DUST), Items.GLOWSTONE, false)
                .add(itemId(Items.AMETHYST_SHARD), Items.AMETHYST_BLOCK, false)
                .add(itemId(Items.MAGMA_CREAM), Items.MAGMA_BLOCK, false)
                .add(itemId(Items.CLAY_BALL), Items.CLAY, false)
                .add(itemId(Items.MELON_SLICE), Items.MELON, false)
                .add(itemId(Items.ICE), Items.PACKED_ICE, false)
                .add(itemId(Items.PACKED_ICE), Items.BLUE_ICE, false)
                .add(itemId(Items.STRING), Items.WHITE_WOOL, false)
                .add(itemId(Items.SNOWBALL), Items.SNOW_BLOCK, false)
                .add(itemId(Items.HONEYCOMB), Items.HONEYCOMB_BLOCK, false)
                .add(itemId(Items.POINTED_DRIPSTONE), Items.DRIPSTONE_BLOCK, false);
    }

    private static ResourceLocation itemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}

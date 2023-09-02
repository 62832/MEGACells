package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATags;

public class CommonTagProvider {
    public static class ItemTags extends ItemTagsProvider {
        public ItemTags(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                CompletableFuture<TagLookup<Block>> blockTags) {
            super(output, registries, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(MEGATags.SKY_STEEL_INGOT).add(MEGAItems.SKY_STEEL_INGOT.asItem());
            tag(MEGATags.SKY_STEEL_BLOCK_ITEM).add(MEGABlocks.SKY_STEEL_BLOCK.asItem());

            tag(P2PTunnelAttunement.getAttunementTag(P2PTunnelAttunement.ENERGY_TUNNEL))
                    .add(MEGABlocks.MEGA_ENERGY_CELL.asItem());
            tag(MEGATags.MEGA_PATTERN_PROVIDER)
                    .add(MEGABlocks.MEGA_PATTERN_PROVIDER.asItem())
                    .add(MEGAItems.MEGA_PATTERN_PROVIDER.asItem());

            tag(MEGATags.COMPRESSION_OVERRIDES)
                    .add(Items.QUARTZ)
                    .add(Items.GLOWSTONE_DUST)
                    .add(Items.AMETHYST_SHARD)
                    .add(Items.MAGMA_CREAM)
                    .add(Items.CLAY_BALL)
                    .add(Items.MELON_SLICE)
                    .add(Items.ICE, Items.PACKED_ICE)
                    .addOptionalTag(new ResourceLocation("functionalstorage", "ignore_crafting_check"));
        }
    }

    public static class BlockTags extends IntrinsicHolderTagsProvider<Block> {
        public BlockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, Registries.BLOCK, registries, block -> BuiltInRegistries.BLOCK
                    .getResourceKey(block)
                    .orElseThrow());
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            for (var block : MEGABlocks.getBlocks()) {
                tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
            }

            tag(MEGATags.SKY_STEEL_BLOCK).add(MEGABlocks.SKY_STEEL_BLOCK.block());
        }
    }
}

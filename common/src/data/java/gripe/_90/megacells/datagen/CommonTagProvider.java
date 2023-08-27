package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATags;

public class CommonTagProvider {
    public static class Items extends ItemTagsProvider {
        public Items(
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
        }
    }

    public static class Blocks extends IntrinsicHolderTagsProvider<Block> {
        public Blocks(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, Registries.BLOCK, registries, block -> BuiltInRegistries.BLOCK
                    .getResourceKey(block)
                    .orElseThrow());
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            for (var block : MEGABlocks.getBlocks()) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
            }

            tag(MEGATags.SKY_STEEL_BLOCK).add(MEGABlocks.SKY_STEEL_BLOCK.block());
        }
    }
}

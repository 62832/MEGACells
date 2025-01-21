package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATags;

public class MEGATagProvider {
    public static class Blocks extends IntrinsicHolderTagsProvider<Block> {
        public Blocks(
                PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existing) {
            super(
                    output,
                    Registries.BLOCK,
                    registries,
                    block -> BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow(),
                    MEGACells.MODID,
                    existing);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            for (var block : MEGABlocks.getBlocks()) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
            }

            tag(MEGATags.SKY_STEEL_BLOCK).add(MEGABlocks.SKY_STEEL_BLOCK.block());
            tag(MEGATags.SKY_BRONZE_BLOCK).add(MEGABlocks.SKY_BRONZE_BLOCK.block());
            tag(MEGATags.SKY_OSMIUM_BLOCK).add(MEGABlocks.SKY_OSMIUM_BLOCK.block());

            tag(Tags.Blocks.STORAGE_BLOCKS)
                    .addTag(MEGATags.SKY_STEEL_BLOCK)
                    .addTag(MEGATags.SKY_BRONZE_BLOCK)
                    .addTag(MEGATags.SKY_OSMIUM_BLOCK);
        }

        @NotNull
        @Override
        public String getName() {
            return "Tags (Block)";
        }
    }

    public static class Items extends ItemTagsProvider {
        public Items(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                CompletableFuture<TagsProvider.TagLookup<net.minecraft.world.level.block.Block>> blockTags,
                ExistingFileHelper existing) {
            super(output, registries, blockTags, MEGACells.MODID, existing);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            copy(MEGATags.SKY_STEEL_BLOCK);
            copy(MEGATags.SKY_BRONZE_BLOCK);
            copy(MEGATags.SKY_OSMIUM_BLOCK);

            tag(MEGATags.SKY_STEEL_INGOT).add(MEGAItems.SKY_STEEL_INGOT.asItem());
            tag(MEGATags.SKY_BRONZE_INGOT).add(MEGAItems.SKY_BRONZE_INGOT.asItem());
            tag(MEGATags.SKY_OSMIUM_INGOT).add(MEGAItems.SKY_OSMIUM_INGOT.asItem());

            tag(P2PTunnelAttunement.getAttunementTag(P2PTunnelAttunement.ENERGY_TUNNEL))
                    .add(MEGABlocks.MEGA_ENERGY_CELL.asItem());

            tag(MEGATags.MEGA_INTERFACE).add(MEGABlocks.MEGA_INTERFACE.asItem(), MEGAItems.MEGA_INTERFACE.asItem());
            tag(MEGATags.MEGA_PATTERN_PROVIDER)
                    .add(MEGABlocks.MEGA_PATTERN_PROVIDER.asItem(), MEGAItems.MEGA_PATTERN_PROVIDER.asItem());

            tag(Tags.Items.INGOTS)
                    .addTag(MEGATags.SKY_STEEL_INGOT)
                    .addTag(MEGATags.SKY_BRONZE_INGOT)
                    .addTag(MEGATags.SKY_OSMIUM_INGOT);
            copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        }

        private void copy(TagKey<Block> blockTag) {
            copy(blockTag, TagKey.create(Registries.ITEM, blockTag.location()));
        }

        @NotNull
        @Override
        public String getName() {
            return "Tags (Item)";
        }
    }
}

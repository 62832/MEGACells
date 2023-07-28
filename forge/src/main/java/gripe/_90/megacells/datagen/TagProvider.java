package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.features.P2PTunnelAttunement;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.definition.MEGATags;
import gripe._90.megacells.util.Utils;

abstract class TagProvider {
    static class Items extends ItemTagsProvider {
        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existing) {
            super(output, registries, blockTags, Utils.MODID, existing);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            tag(P2PTunnelAttunement.getAttunementTag(P2PTunnelAttunement.ENERGY_TUNNEL))
                    .add(getKey(MEGABlocks.MEGA_ENERGY_CELL));
            tag(MEGATags.MEGA_PATTERN_PROVIDER)
                    .add(getKey(MEGABlocks.MEGA_PATTERN_PROVIDER))
                    .add(getKey(MEGAParts.MEGA_PATTERN_PROVIDER));
        }

        private ResourceKey<Item> getKey(ItemDefinition<?> item) {
            return ForgeRegistries.ITEMS.getResourceKey(item.asItem()).orElse(null);
        }
    }

    static class Blocks extends BlockTagsProvider {
        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                @Nullable ExistingFileHelper existing) {
            super(output, registries, Utils.MODID, existing);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            MEGABlocks.getBlocks().forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(getKey(block)));
        }

        private ResourceKey<Block> getKey(BlockDefinition<?> block) {
            return ForgeRegistries.BLOCKS.getResourceKey(block.block()).orElse(null);
        }
    }
}

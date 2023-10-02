package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.definition.MEGATags;

abstract class TagProvider {
    static class ItemTags extends FabricTagProvider.ItemTagProvider {
        ItemTags(FabricDataGenerator gen, BlockTagProvider block) {
            super(gen, block);
        }

        @Override
        protected void generateTags() {
            tag(P2PTunnelAttunement.getAttunementTag(P2PTunnelAttunement.ENERGY_TUNNEL))
                    .add(MEGABlocks.MEGA_ENERGY_CELL.asItem());
            tag(MEGATags.MEGA_PATTERN_PROVIDER)
                    .add(MEGABlocks.MEGA_PATTERN_PROVIDER.asItem())
                    .add(MEGAParts.MEGA_PATTERN_PROVIDER.asItem());
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

    static class BlockTags extends FabricTagProvider.BlockTagProvider {
        BlockTags(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        protected void generateTags() {
            MEGABlocks.getBlocks().forEach(block -> tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(block.block()));
        }
    }
}

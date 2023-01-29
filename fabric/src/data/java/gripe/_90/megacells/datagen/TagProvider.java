package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.definition.MEGATags;

abstract class TagProvider {
    static class Items extends FabricTagProvider.ItemTagProvider {
        Items(FabricDataGenerator gen, BlockTagProvider block) {
            super(gen, block);
        }

        @Override
        protected void generateTags() {
            tag(P2PTunnelAttunement.getAttunementTag(P2PTunnelAttunement.ENERGY_TUNNEL))
                    .add(MEGABlocks.MEGA_ENERGY_CELL.asItem());
            tag(MEGATags.MEGA_PATTERN_PROVIDER)
                    .add(MEGABlocks.MEGA_PATTERN_PROVIDER.asItem())
                    .add(MEGAParts.MEGA_PATTERN_PROVIDER.asItem());
        }
    }

    static class Blocks extends FabricTagProvider.BlockTagProvider {
        Blocks(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        protected void generateTags() {
            MEGABlocks.getBlocks().forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block()));
        }
    }
}

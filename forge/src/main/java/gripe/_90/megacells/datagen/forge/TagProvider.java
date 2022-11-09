package gripe._90.megacells.datagen.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.util.Utils;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.definition.MEGATags;

public class TagProvider {
    static class Items extends ItemTagsProvider {
        public Items(DataGenerator gen, BlockTagsProvider block, ExistingFileHelper efh) {
            super(gen, block, Utils.MODID, efh);
        }

        @Override
        protected void addTags() {
            tag(P2PTunnelAttunement.getAttunementTag(P2PTunnelAttunement.ENERGY_TUNNEL))
                    .add(MEGABlocks.MEGA_ENERGY_CELL.asItem());
            tag(MEGATags.MEGA_PATTERN_PROVIDER)
                    .add(MEGABlocks.MEGA_PATTERN_PROVIDER.asItem())
                    .add(MEGAParts.MEGA_PATTERN_PROVIDER.asItem());
        }
    }

    static class Blocks extends BlockTagsProvider {
        public Blocks(DataGenerator gen, @Nullable ExistingFileHelper efh) {
            super(gen, Utils.MODID, efh);
        }

        @Override
        protected void addTags() {
            MEGABlocks.getBlocks().forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block()));
        }
    }
}

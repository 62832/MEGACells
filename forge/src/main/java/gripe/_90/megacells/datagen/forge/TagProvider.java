package gripe._90.megacells.datagen.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.util.Utils;

public class TagProvider {
    static class Items extends ItemTagsProvider {
        public Items(DataGenerator gen, BlockTagsProvider block, ExistingFileHelper efh) {
            super(gen, block, Utils.MODID, efh);
        }

        @Override
        protected void addTags() {

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

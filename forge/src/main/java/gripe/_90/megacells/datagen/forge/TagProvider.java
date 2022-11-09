package gripe._90.megacells.datagen.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;

public class TagProvider extends ItemTagsProvider {
    public TagProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, new BlockTagProvider(gen, efh), MEGACells.MODID, efh);
    }

    @Override
    protected void addTags() {

    }

    static class BlockTagProvider extends BlockTagsProvider {
        public BlockTagProvider(DataGenerator gen, @Nullable ExistingFileHelper efh) {
            super(gen, MEGACells.MODID, efh);
        }

        @Override
        protected void addTags() {
            for (var block : MEGABlocks.getBlocks()) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
            }
        }
    }
}

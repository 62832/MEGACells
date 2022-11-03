package gripe._90.megacells.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;

import gripe._90.megacells.block.MEGABlocks;

public class BlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {
    public BlockTagsProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void addTags() {
        for (var block : MEGABlocks.BLOCKS) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
        }
    }
}

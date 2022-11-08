package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;

public class BlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {
    public BlockTagsProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, MEGACells.MODID, efh);
    }

    @Override
    protected void addTags() {
        for (var block : MEGABlocks.getBlocks()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
        }
    }
}

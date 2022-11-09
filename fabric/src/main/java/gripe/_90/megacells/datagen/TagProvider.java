package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;

import gripe._90.megacells.definition.MEGABlocks;

public class TagProvider extends FabricTagProvider.ItemTagProvider {
    public TagProvider(FabricDataGenerator gen) {
        super(gen, new BlockTagProvider(gen));
    }

    @Override
    protected void generateTags() {

    }

    static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public BlockTagProvider(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        protected void generateTags() {
            for (var block : MEGABlocks.getBlocks()) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
            }
        }
    }
}

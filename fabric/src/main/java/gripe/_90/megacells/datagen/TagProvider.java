package gripe._90.megacells.datagen;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;

import gripe._90.megacells.definition.MEGABlocks;

public class TagProvider {
    static class Items extends FabricTagProvider.ItemTagProvider {
        public Items(FabricDataGenerator gen, @Nullable BlockTagProvider block) {
            super(gen, block);
        }

        @Override
        protected void generateTags() {

        }
    }

    static class Blocks extends FabricTagProvider.BlockTagProvider {
        public Blocks(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        protected void generateTags() {
            MEGABlocks.getBlocks().forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block()));
        }
    }
}

package gripe._90.megacells.init.loader;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.IForgeRegistry;

import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.block.entity.MEGABlockEntities;
import gripe._90.megacells.item.MEGAItems;

public class Registration {

    public static void initBlocks(IForgeRegistry<Block> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            Block block = definition.asBlock();
            registry.register(definition.getId(), block);
        }
    }

    public static void initItems(IForgeRegistry<Item> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            var item = definition.asItem();
            registry.register(definition.getId(), item);
        }
        for (var definition : MEGAItems.getItems()) {
            var item = definition.asItem();
            registry.register(definition.getId(), item);
        }
    }

    public static void initBlockEntities(IForgeRegistry<BlockEntityType<?>> registry) {
        for (var be : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            registry.register(be.getKey(), be.getValue());
        }
    }

}

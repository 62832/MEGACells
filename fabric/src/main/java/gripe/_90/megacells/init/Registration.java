package gripe._90.megacells.init;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.block.entity.MEGABlockEntities;
import gripe._90.megacells.item.MEGAItems;

public class Registration {

    public static void registerBlocks(Registry<Block> registry) {
        for (var definition : MEGABlocks.BLOCKS) {
            Block block = definition.block();
            Registry.register(registry, definition.id(), block);
        }
    }

    public static void registerItems(Registry<Item> registry) {
        for (var definition : MEGABlocks.BLOCKS) {
            var item = definition.asItem();
            Registry.register(registry, definition.id(), item);
        }
        for (var definition : MEGAItems.ITEMS) {
            var item = definition.asItem();
            Registry.register(registry, definition.id(), item);
        }
    }

    public static void registerBlockEntities(Registry<BlockEntityType<?>> registry) {
        for (var be : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            Registry.register(registry, be.getKey(), be.getValue());
        }
    }

}

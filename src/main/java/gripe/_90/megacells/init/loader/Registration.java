package gripe._90.megacells.init.loader;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.block.entity.MEGABlockEntities;
import gripe._90.megacells.item.MEGAItems;

public class Registration {

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        initBlocks(event.getRegistry());
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        initItems(event.getRegistry());
    }

    public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        initBlockEntities(event.getRegistry());
    }

    public static void initBlocks(IForgeRegistry<Block> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            Block block = definition.asBlock();
            block.setRegistryName(definition.getId());
            registry.register(block);
        }
    }

    public static void initItems(IForgeRegistry<Item> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            var item = definition.asItem();
            item.setRegistryName(definition.getId());
            registry.register(item);
        }
        for (var definition : MEGAItems.getItems()) {
            var item = definition.asItem();
            item.setRegistryName(definition.getId());
            registry.register(item);
        }
    }

    public static void initBlockEntities(IForgeRegistry<BlockEntityType<?>> registry) {
        for (var be : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            registry.register(be.getValue());
        }
    }
}

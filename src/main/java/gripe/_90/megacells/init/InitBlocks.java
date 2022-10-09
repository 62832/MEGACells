package gripe._90.megacells.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import gripe._90.megacells.block.MEGABlocks;

public class InitBlocks {
    public static void init(IForgeRegistry<Block> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            Block block = definition.asBlock();
            block.setRegistryName(definition.getId());
            registry.register(block);
        }
    }

    public static void register(RegistryEvent.Register<Block> event) {
        init(event.getRegistry());
    }
}

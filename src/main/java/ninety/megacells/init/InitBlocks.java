package ninety.megacells.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

import ninety.megacells.block.MEGABlocks;

public class InitBlocks {
    public static void init(IForgeRegistry<Block> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            Block block = definition.asBlock();
            registry.register(definition.getId(), block);
        }
    }
}

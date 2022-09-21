package ninety.megacells.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import ninety.megacells.block.MEGABlocks;
import ninety.megacells.item.MEGAItems;

public class InitItems {
    public static void init(IForgeRegistry<Item> registry) {
        for (var definition : MEGABlocks.getBlocks()) {
            var item = definition.asItem();
            registry.register(definition.getId(), item);
        }
        for (var definition : MEGAItems.getItems()) {
            var item = definition.asItem();
            registry.register(definition.getId(), item);
        }
    }
}

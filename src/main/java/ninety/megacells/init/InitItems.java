package ninety.megacells.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import ninety.megacells.block.MEGABlocks;
import ninety.megacells.item.MEGAItems;

public class InitItems {
    public static void init(IForgeRegistry<Item> registry) {
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

    public static void register(RegistryEvent.Register<Item> event) {
        init(event.getRegistry());
    }
}

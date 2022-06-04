package ninety.megacells.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import ninety.megacells.block.entity.MEGABlockEntities;

public class InitBlockEntities {
    public static void init(IForgeRegistry<BlockEntityType<?>> registry) {
        for (var be : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            registry.register(be.getValue());
        }
    }

    public static void register(RegistryEvent.Register<BlockEntityType<?>> event) {
        init(event.getRegistry());
    }
}

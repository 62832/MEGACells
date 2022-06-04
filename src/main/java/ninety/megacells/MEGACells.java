package ninety.megacells;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.datagen.MEGADataGenerators;
import ninety.megacells.init.*;
import ninety.megacells.init.client.*;

@Mod(MEGACells.MODID)
public class MEGACells {

    public static final String MODID = "megacells";

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MEGACells.MODID, path);
    }

    public MEGACells() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addGenericListener(Item.class, InitItems::register);
        bus.addGenericListener(Block.class, InitBlocks::register);
        bus.addGenericListener(BlockEntityType.class, InitBlockEntities::register);

        bus.addListener(MEGADataGenerators::onGatherData);
        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(InitCellModels::init);
            event.enqueueWork(InitUpgrades::init);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitAutoRotatingModel::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitBlockEntityRenderers::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitBuiltInModels::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitItemColors::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitRenderTypes::init);
    }
}

package ninety.megacells;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.datagen.MEGADataGenerators;
import ninety.megacells.init.InitCellModels;
import ninety.megacells.init.InitUpgrades;
import ninety.megacells.init.client.InitItemColors;
import ninety.megacells.item.MEGAItems;

@Mod(MEGACells.MODID)
public class MEGACells {

    public static final String MODID = "megacells";

    public MEGACells() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MEGAItems.init(bus);

        bus.addListener(MEGADataGenerators::onGatherData);
        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(InitCellModels::init);
            event.enqueueWork(InitUpgrades::init);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitItemColors::initialize);
    }
}

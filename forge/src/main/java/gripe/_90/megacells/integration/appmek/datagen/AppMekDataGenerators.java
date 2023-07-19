package gripe._90.megacells.integration.appmek.datagen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.megacells.util.Utils;

@Mod.EventBusSubscriber(modid = Utils.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AppMekDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        if (Utils.PLATFORM.isModLoaded("appmek")) {
            var pack = event.getGenerator().getVanillaPack(true);

            pack.addProvider(packOutput -> new AppMekItemModelProvider(packOutput, event.getExistingFileHelper()));
            pack.addProvider(AppMekRecipeProvider::new);
        }
    }
}

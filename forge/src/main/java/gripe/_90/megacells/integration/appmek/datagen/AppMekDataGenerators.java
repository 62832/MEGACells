package gripe._90.megacells.integration.appmek.datagen;

import net.minecraftforge.data.event.GatherDataEvent;

import gripe._90.megacells.util.Utils;

public class AppMekDataGenerators {
    public static void onGatherData(GatherDataEvent event) {
        if (Utils.PLATFORM.isModLoaded("appmek")) {
            var pack = event.getGenerator().getVanillaPack(true);

            pack.addProvider(packOutput -> new AppMekItemModelProvider(packOutput, event.getExistingFileHelper()));
            pack.addProvider(AppMekRecipeProvider::new);
        }
    }
}

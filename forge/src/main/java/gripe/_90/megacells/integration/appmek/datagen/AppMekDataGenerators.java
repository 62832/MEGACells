package gripe._90.megacells.integration.appmek.datagen;

import net.minecraftforge.data.event.GatherDataEvent;

import gripe._90.megacells.util.Utils;

public class AppMekDataGenerators {
    public static void onGatherData(GatherDataEvent event) {
        if (Utils.PLATFORM.isModLoaded("appmek")) {
            var generator = event.getGenerator();

            generator.addProvider(true, new AppMekItemModelProvider(generator, event.getExistingFileHelper()));
            generator.addProvider(true, new AppMekRecipeProvider(generator));
        }
    }
}

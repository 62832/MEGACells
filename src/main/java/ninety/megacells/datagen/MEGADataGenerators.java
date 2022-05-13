package ninety.megacells.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.MEGAMekIntegration;
import ninety.megacells.integration.appmek.data.MEGAMekBlockModelProvider;
import ninety.megacells.integration.appmek.data.MEGAMekItemModelProvider;
import ninety.megacells.integration.appmek.data.MEGAMekRecipeProvider;

@Mod.EventBusSubscriber(modid = MEGACells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MEGADataGenerators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(new MEGAItemModelProvider(generator, existingFileHelper));
        generator.addProvider(new MEGABlockModelProvider(generator, existingFileHelper));
        generator.addProvider(new MEGARecipeProvider(generator));

        if (MEGAMekIntegration.isLoaded()) {
            generator.addProvider(new MEGAMekItemModelProvider(generator, existingFileHelper));
            generator.addProvider(new MEGAMekBlockModelProvider(generator, existingFileHelper));
            generator.addProvider(new MEGAMekRecipeProvider(generator));
        }
    }
}

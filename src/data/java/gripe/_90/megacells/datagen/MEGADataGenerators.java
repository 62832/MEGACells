package gripe._90.megacells.datagen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import gripe._90.megacells.MEGACells;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MEGACells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MEGADataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        var existing = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new MEGAModelProvider(output, existing));

        var registries = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new MEGATagProvider.BlockTags(output, registries, existing));
        generator.addProvider(event.includeServer(), new MEGATagProvider.ItemTags(output, registries, existing));

        generator.addProvider(event.includeClient(), new MEGALanguageProvider(output));
        generator.addProvider(event.includeServer(), new MEGARecipeProvider(output));
        generator.addProvider(event.includeServer(), new MEGALootProvider(output));
    }
}

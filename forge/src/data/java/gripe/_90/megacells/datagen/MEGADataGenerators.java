package gripe._90.megacells.datagen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.megacells.MEGACells;

@Mod.EventBusSubscriber(modid = MEGACells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MEGADataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        var existing = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ModelProvider.Items(output, existing));
        generator.addProvider(event.includeClient(), new ModelProvider.Blocks(output, existing));
        generator.addProvider(event.includeClient(), new ModelProvider.Parts(output, existing));

        var registries = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new CommonTagProvider.BlockTags(output, registries));
        generator.addProvider(event.includeServer(), new CommonTagProvider.ItemTags(output, registries));

        generator.addProvider(event.includeClient(), new CommonLanguageProvider(output));
        generator.addProvider(event.includeServer(), new CommonLootTableProvider(output));
        generator.addProvider(event.includeServer(), new CommonRecipeProvider(output));

        generator.addProvider(event.includeServer(), new ForgeRecipeProvider(output));
    }
}

package gripe._90.megacells.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import gripe._90.megacells.MEGACells;

@EventBusSubscriber(modid = MEGACells.MODID)
public class MEGADataGenerators {
    @SubscribeEvent
    public static void onGatherClientData(GatherDataEvent.Client event) {
        var output = event.getGenerator().getPackOutput();
        event.addProvider(new MEGALanguageProvider(output));

        // TODO: MEGAModelProvider/OverrideModelProvider still need the new BlockModelGenerators
        // API; see build.gradle.kts.
    }

    @SubscribeEvent
    public static void onGatherServerData(GatherDataEvent.Server event) {
        var output = event.getGenerator().getPackOutput();
        var registries = event.getLookupProvider();

        event.addProvider(new RecipeProviderRunner(output, registries));
        event.addProvider(new MEGALootProvider(output, registries));
        event.addProvider(new MEGADataMapProvider(output, registries));

        var blockTags = new MEGATagProvider.Blocks(output, registries);
        event.addProvider(blockTags);
        event.addProvider(new MEGATagProvider.Items(output, registries, blockTags.contentsGetter()));
    }
}

package gripe._90.megacells.datagen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.datagen.integration.DummyObjects;

@Mod(value = MEGACells.MODID, dist = Dist.CLIENT)
public class MEGADataGenerators {
    public MEGADataGenerators(IEventBus eventBus) {
        if (DatagenModLoader.isRunningDataGen()) {
            DummyObjects.init(eventBus);
            eventBus.addListener(MEGADataGenerators::onGatherClientData);
        }
    }

    private static void onGatherClientData(GatherDataEvent.Client event) {
        var output = event.getGenerator().getPackOutput();
        event.addProvider(new MEGALanguageProvider(output));
        event.addProvider(new MEGAModelProvider(output));
        event.addProvider(new OverrideModelProvider(event.getGenerator().getPackOutput("optional_cell_colours")));

        var registries = event.getLookupProvider();
        event.addProvider(new MEGARecipeProvider(output, registries));
        event.addProvider(new MEGALootProvider(output, registries));
        event.addProvider(new MEGADataMapProvider(output, registries));

        var blockTags = new MEGATagProvider.Blocks(output, registries);
        event.addProvider(blockTags);
        event.addProvider(new MEGATagProvider.Items(output, registries, blockTags.contentsGetter()));
    }
}

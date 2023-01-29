package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MEGADataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var blockTags = new TagProvider.Blocks(generator);
        generator.addProvider(blockTags);
        generator.addProvider(new TagProvider.Items(generator, blockTags));

        generator.addProvider(LootTableProvider::new);
        generator.addProvider(ModelProvider::new);
        generator.addProvider(RecipeProvider::new);

        for (var en : new String[] { "en_us", "en_gb", "en_au", "en_ca", "en_nz" }) {
            generator.addProvider(new LocalisationProvider(generator, en));
        }
    }
}

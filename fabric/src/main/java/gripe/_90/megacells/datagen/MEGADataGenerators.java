package gripe._90.megacells.datagen;

import java.util.List;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MEGADataGenerators implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.addProvider(TagProvider::new);
        generator.addProvider(LootTableProvider::new);
        generator.addProvider(ModelProvider::new);
        generator.addProvider(RecipeProvider::new);

        for (var en : List.of("en_us", "en_gb", "en_au", "en_ca", "en_nz")) {
            generator.addProvider(new LocalisationProvider(generator, en));
        }
    }
}

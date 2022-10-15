package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MEGADataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.addProvider(MEGAModelProvider::new);
        generator.addProvider(MEGARecipeProvider::new);
    }
}

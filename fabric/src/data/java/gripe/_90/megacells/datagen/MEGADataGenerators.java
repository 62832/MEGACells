package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class MEGADataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();

        pack.addProvider(CommonTagProvider.BlockTags::new);
        pack.addProvider(CommonTagProvider.ItemTags::new);

        pack.addProvider((FabricDataOutput output) -> new CommonLanguageProvider(output));
        pack.addProvider((FabricDataOutput output) -> new CommonLootTableProvider(output));
        pack.addProvider((FabricDataOutput output) -> new CommonRecipeProvider(output));
        pack.addProvider((FabricDataOutput output) -> new CommonModelProvider(output));
    }
}

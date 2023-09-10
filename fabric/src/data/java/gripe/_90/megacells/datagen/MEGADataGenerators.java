package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.data.registries.VanillaRegistries;

public class MEGADataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();
        var registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        var blocks = pack.addProvider((FabricDataOutput output) -> new CommonTagProvider.BlockTags(output, registries));
        pack.addProvider((FabricDataOutput output) ->
                new CommonTagProvider.ItemTags(output, registries, blocks.contentsGetter()));

        pack.addProvider((FabricDataOutput output) -> new CommonLanguageProvider(output));
        pack.addProvider((FabricDataOutput output) -> new CommonLootTableProvider(output));
        pack.addProvider(ModelProvider::new);
        pack.addProvider(RecipeProvider::new);
    }
}

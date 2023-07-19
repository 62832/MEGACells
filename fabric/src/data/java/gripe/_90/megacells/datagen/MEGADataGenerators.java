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

        var blockTagsProvider = pack
                .addProvider((FabricDataOutput packOutput) -> new TagProvider.Blocks(packOutput, registries));
        pack.addProvider(
                (FabricDataOutput packOutput) -> new TagProvider.Items(packOutput, registries, blockTagsProvider));

        pack.addProvider(LootTableProvider::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(RecipeProvider::new);
    }
}

package gripe._90.megacells.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.Util;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.megacells.MEGACells;

@Mod.EventBusSubscriber(modid = MEGACells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MEGADataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var pack = event.getGenerator().getVanillaPack(true);
        var existing = event.getExistingFileHelper();
        var registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        var blockTags = pack.addProvider(output -> new TagProvider.Blocks(output, registries, existing));
        pack.addProvider(output -> new TagProvider.Items(output, registries, blockTags.contentsGetter(), existing));

        pack.addProvider(output -> new ModelProvider.Items(output, existing));
        pack.addProvider(output -> new ModelProvider.Blocks(output, existing));
        pack.addProvider(output -> new ModelProvider.Parts(output, existing));

        pack.addProvider(RecipeProvider::new);
        pack.addProvider(LootTableProvider::new);
        pack.addProvider(LocalisationProvider::new);
    }
}

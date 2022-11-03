package gripe._90.megacells.datagen.forge;

import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.appmek.datagen.AppMekBlockModelProvider;
import gripe._90.megacells.integration.appmek.datagen.AppMekItemModelProvider;
import gripe._90.megacells.integration.appmek.datagen.AppMekRecipeProvider;

@Mod.EventBusSubscriber(modid = MEGACells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MEGADataGenerators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(true, new LootTableProvider(generator));

        generator.addProvider(true, new BlockModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new BlockStateProvider(generator, existingFileHelper));
        generator.addProvider(true, new ItemModelProvider(generator, existingFileHelper));

        generator.addProvider(true, new RecipeProvider(generator));
        generator.addProvider(true, new BlockTagsProvider(generator, existingFileHelper));

        for (var en : List.of("en_us", "en_gb", "en_au", "en_ca", "en_nz")) {
            generator.addProvider(true, new LocalisationProvider(generator, en));
        }

        generator.addProvider(true, new AppMekBlockModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new AppMekItemModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new AppMekRecipeProvider(generator));
    }
}

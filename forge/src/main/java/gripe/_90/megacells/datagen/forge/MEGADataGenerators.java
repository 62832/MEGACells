package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.datagen.BlockModelProvider;
import gripe._90.megacells.datagen.ItemModelProvider;
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
        generator.addProvider(true, new ItemModelProvider(generator, existingFileHelper));

        generator.addProvider(true, new RecipeProvider(generator));
        generator.addProvider(true, new BlockTagsProvider(generator, existingFileHelper));

        generator.addProvider(true, new AppMekItemModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new AppMekRecipeProvider(generator));
    }
}

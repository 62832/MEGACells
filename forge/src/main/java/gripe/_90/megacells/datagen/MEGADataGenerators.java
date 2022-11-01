package gripe._90.megacells.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.megacells.MEGACells;

@Mod.EventBusSubscriber(modid = MEGACells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MEGADataGenerators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(true, new MEGAItemModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new MEGABlockModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new MEGARecipeProvider(generator));
    }
}

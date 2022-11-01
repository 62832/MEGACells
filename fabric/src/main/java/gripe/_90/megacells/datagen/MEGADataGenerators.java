package gripe._90.megacells.datagen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

@SuppressWarnings("unused")
public class MEGADataGenerators implements DataGeneratorEntrypoint {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void onGatherData(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        generator.addProvider(true, new BlockDropProvider(generator.getOutputFolder()));

        generator.addProvider(true, new BlockModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new ItemModelProvider(generator, existingFileHelper));

        generator.addProvider(true, new RecipeProvider(generator));
        generator.addProvider(true, new BlockTagsProvider(generator));
    }

    public static void dump(Path outputPath, List<Path> existingDataPaths) throws Exception {
        LOGGER.info("Writing generated resources to {}", outputPath.toAbsolutePath());

        DataGenerator generator = new DataGenerator(outputPath, Collections.emptyList(),
                SharedConstants.getCurrentVersion(), true);
        var existingFileHelper = new ExistingFileHelper(existingDataPaths, Collections.emptySet(),
                true, null, null);
        onGatherData(generator, existingFileHelper);
        generator.run();
    }

    public static void runIfEnabled() {
        var outputPath = Paths.get(System.getProperty("megacells.generateData.outputPath"));
        var existingData = System.getProperty("megacells.generateData.existingData").split(";");
        var existingDataPaths = Arrays.stream(existingData).map(Paths::get).toList();

        try {
            dump(outputPath, existingDataPaths);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        runIfEnabled();
    }
}

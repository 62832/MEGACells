package gripe._90.megacells.init.forge.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.block.AEBaseBlock;
import appeng.client.render.crafting.MonitorBakedModel;
import appeng.client.render.model.AutoRotatingBakedModel;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.util.Utils;

public class InitAutoRotatingModel {
    private static final Map<String, Function<BakedModel, BakedModel>> CUSTOMIZERS = new HashMap<>();

    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitAutoRotatingModel::initAutoRotatingModels);
        bus.addListener(InitAutoRotatingModel::onModelBake);
    }

    public static void initAutoRotatingModels(ModelEvent.BakingCompleted ignoredEvent) {
        register(MEGABlocks.CRAFTING_MONITOR, InitAutoRotatingModel::customizeCraftingMonitorModel);

        var noAutoRotation = ImmutableSet.of(
                MEGABlocks.MEGA_CRAFTING_UNIT,
                MEGABlocks.CRAFTING_ACCELERATOR,
                MEGABlocks.CRAFTING_STORAGE_1M,
                MEGABlocks.CRAFTING_STORAGE_4M,
                MEGABlocks.CRAFTING_STORAGE_16M,
                MEGABlocks.CRAFTING_STORAGE_64M,
                MEGABlocks.CRAFTING_STORAGE_256M,
                MEGABlocks.CRAFTING_MONITOR);

        for (var block : MEGABlocks.getBlocks()) {
            if (noAutoRotation.contains(block)) {
                continue;
            }

            if (block.block() instanceof AEBaseBlock) {
                // This is a default rotating model if the base-block uses an AE block entity
                // which exposes UP/FRONT as extended props
                register(block, AutoRotatingBakedModel::new);
            }
        }
    }

    private static void register(BlockDefinition<?> block, Function<BakedModel, BakedModel> customizer) {
        String path = block.id().getPath();
        CUSTOMIZERS.put(path, customizer);
    }

    private static BakedModel customizeCraftingMonitorModel(BakedModel model) {
        // The formed model handles rotations itself, the unformed one does not
        if (model instanceof MonitorBakedModel) {
            return model;
        }
        return new AutoRotatingBakedModel(model);
    }

    private static void onModelBake(ModelEvent.BakingCompleted event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        Set<ResourceLocation> keys = Sets.newHashSet(modelRegistry.keySet());
        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);

        for (ResourceLocation location : keys) {
            if (!location.getNamespace().equals(Utils.MODID)) {
                continue;
            }

            BakedModel orgModel = modelRegistry.get(location);

            // Don't customize the missing model. This causes Forge to swallow exceptions
            if (orgModel == missingModel) {
                continue;
            }

            Function<BakedModel, BakedModel> customizer = CUSTOMIZERS.get(location.getPath());
            if (customizer != null) {
                BakedModel newModel = customizer.apply(orgModel);

                if (newModel != orgModel) {
                    modelRegistry.put(location, newModel);
                }
            }
        }
    }
}

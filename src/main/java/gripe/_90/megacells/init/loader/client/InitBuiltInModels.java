package gripe._90.megacells.init.loader.client;

import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.client.render.SimpleModelLoader;
import appeng.client.render.crafting.CraftingCubeModel;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;

public class InitBuiltInModels {

    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitBuiltInModels::initModels);
    }

    private static void initModels(ModelRegistryEvent event) {
        for (var type : MEGACraftingUnitType.values()) {
            craftingModel(type);
        }
    }

    private static void craftingModel(MEGACraftingUnitType type) {
        addBuiltInModel("block/crafting/" + type.getAffix() + "_formed",
                () -> new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));
    }

    private static <T extends IModelGeometry<T>> void addBuiltInModel(String id, Supplier<T> modelFactory) {
        ModelLoaderRegistry.registerLoader(MEGACells.makeId(id), new SimpleModelLoader<>(modelFactory));
    }
}

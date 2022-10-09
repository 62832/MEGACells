package gripe._90.megacells.init.client;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;

import appeng.client.render.SimpleModelLoader;
import appeng.client.render.crafting.CraftingCubeModel;

@OnlyIn(Dist.CLIENT)
public class InitBuiltInModels {

    private static BiConsumer<String, IGeometryLoader<?>> register = null;

    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitBuiltInModels::initModels);
    }

    private static void initModels(ModelEvent.RegisterGeometryLoaders event) {
        registerModels(event::register);
    }

    private static void registerModels(BiConsumer<String, IGeometryLoader<?>> register) {
        InitBuiltInModels.register = register;

        for (var type : MEGACraftingUnitType.values()) {
            addBuiltInModel("block/crafting/" + type.getAffix() + "_formed",
                    () -> new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));
        }

        InitBuiltInModels.register = null;
    }

    private static <T extends IUnbakedGeometry<T>> void addBuiltInModel(String id, Supplier<T> modelFactory) {
        register.accept(id, new SimpleModelLoader<>(modelFactory));
    }
}

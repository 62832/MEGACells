package gripe._90.megacells.init.forge.client;

import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.client.render.SimpleModelLoader;
import appeng.client.render.crafting.CraftingCubeModel;

import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;

public class InitBuiltInModels {

    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitBuiltInModels::initModels);
    }

    private static void initModels(ModelEvent.RegisterGeometryLoaders event) {
        for (var type : MEGACraftingUnitType.values()) {
            event.register("block/crafting/" + type.getAffix() + "_formed",
                    new SimpleModelLoader<>(() -> new CraftingCubeModel(new MEGACraftingUnitModelProvider(type))));
        }
    }
}

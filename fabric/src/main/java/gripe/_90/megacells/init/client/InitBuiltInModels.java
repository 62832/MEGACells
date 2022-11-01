package gripe._90.megacells.init.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

import appeng.client.render.SimpleModelLoader;
import appeng.client.render.crafting.CraftingCubeModel;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;

@Environment(EnvType.CLIENT)
public class InitBuiltInModels {
    public static void init() {
        for (var type : MEGACraftingUnitType.values()) {
            ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new SimpleModelLoader<>(
                    MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                    () -> new CraftingCubeModel(new MEGACraftingUnitModelProvider(type))));
        }
    }
}

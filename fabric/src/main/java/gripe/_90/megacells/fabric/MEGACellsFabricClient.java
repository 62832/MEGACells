package gripe._90.megacells.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

import appeng.api.IAEAddonEntrypoint;

import gripe._90.megacells.init.client.fabric.InitAutoRotatingModel;
import gripe._90.megacells.init.client.fabric.InitBlockEntityRenderers;
import gripe._90.megacells.init.client.fabric.InitBuiltInModels;
import gripe._90.megacells.init.client.fabric.InitItemColors;
import gripe._90.megacells.init.client.fabric.InitItemModelsProperties;
import gripe._90.megacells.init.client.fabric.InitRenderTypes;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unused")
public class MEGACellsFabricClient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        InitAutoRotatingModel.init();
        InitBlockEntityRenderers.init();
        InitBuiltInModels.init();
        InitItemModelsProperties.init();
        InitRenderTypes.init();

        InitItemColors.init(ColorProviderRegistry.ITEM::register);
    }
}

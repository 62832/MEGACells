package gripe._90.megacells;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

import appeng.api.IAEAddonEntrypoint;

import gripe._90.megacells.init.client.InitAutoRotatingModel;
import gripe._90.megacells.init.client.InitBlockEntityRenderers;
import gripe._90.megacells.init.client.InitBuiltInModels;
import gripe._90.megacells.init.client.InitItemColors;
import gripe._90.megacells.init.client.InitItemModelsProperties;
import gripe._90.megacells.init.client.InitRenderTypes;

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

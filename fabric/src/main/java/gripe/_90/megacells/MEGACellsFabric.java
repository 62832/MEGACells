package gripe._90.megacells;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.core.Registry;

import appeng.api.IAEAddonEntrypoint;

import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.init.Registration;
import gripe._90.megacells.init.client.InitAutoRotatingModel;
import gripe._90.megacells.init.client.InitBlockEntityRenderers;
import gripe._90.megacells.init.client.InitBuiltInModels;
import gripe._90.megacells.init.client.InitItemColors;
import gripe._90.megacells.init.client.InitItemModelsProperties;
import gripe._90.megacells.init.client.InitRenderTypes;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGABlockEntities.init();

        Registration.registerBlocks(Registry.BLOCK);
        Registration.registerItems(Registry.ITEM);
        Registration.registerBlockEntities(Registry.BLOCK_ENTITY_TYPE);

        InitStorageCells.init();
        InitUpgrades.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements IAEAddonEntrypoint {
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
}

package gripe._90.megacells;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;

import appeng.api.IAEAddonEntrypoint;
import appeng.core.AppEng;
import appeng.init.client.InitItemModelsProperties;

import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.init.client.InitAutoRotatingModel;
import gripe._90.megacells.init.client.InitBlockEntityRenderers;
import gripe._90.megacells.init.client.InitBuiltInModels;
import gripe._90.megacells.init.client.InitItemColors;
import gripe._90.megacells.init.client.InitRenderTypes;
import gripe._90.megacells.init.client.InitScreens;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.item.cell.CompressionHandler;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;
import gripe._90.megacells.util.Utils;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGAParts.init();
        MEGABlockEntities.init();

        if (Utils.PLATFORM.isModLoaded("appbot")) {
            AppBotItems.init();
        }

        MEGABlocks.getBlocks().forEach(b -> {
            Registry.register(Registry.BLOCK, b.id(), b.block());
            Registry.register(Registry.ITEM, b.id(), b.asItem());
        });
        MEGAItems.getItems().forEach(i -> Registry.register(Registry.ITEM, i.id(), i.asItem()));
        MEGABlockEntities.getBlockEntityTypes().forEach((k, v) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, k, v));

        Registry.register(Registry.MENU, AppEng.makeId("mega_pattern_provider"), MEGAPatternProviderMenu.TYPE);

        InitStorageCells.init();
        InitUpgrades.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> CompressionHandler.INSTANCE.init());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success)
                CompressionHandler.INSTANCE.init();
        });
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client)
                CompressionHandler.INSTANCE.init();
        });
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements IAEAddonEntrypoint {
        @Override
        public void onAe2Initialized() {
            InitAutoRotatingModel.init();
            InitBlockEntityRenderers.init();
            InitBuiltInModels.init();
            InitItemColors.init();
            InitRenderTypes.init();

            // re-init AE2 props for MEGA energy cell
            InitItemModelsProperties.init();

            ClientLifecycleEvents.CLIENT_STARTED.register(InitScreens::init);
        }
    }
}

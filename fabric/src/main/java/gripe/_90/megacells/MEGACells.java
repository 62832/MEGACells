package gripe._90.megacells;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;

import appeng.api.IAEAddonEntrypoint;
import appeng.core.AppEng;

import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.item.cell.CompressionService;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;
import gripe._90.megacells.util.Utils;

public class MEGACells implements IAEAddonEntrypoint {
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

        ServerLifecycleEvents.SERVER_STARTED.register(server -> CompressionService.INSTANCE.load());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                CompressionService.INSTANCE.load();
            }
        });
    }
}

package gripe._90.megacells;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.GridServices;
import appeng.core.AppEng;

import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.crafting.DecompressionPatternDecoder;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGACreativeTab;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.service.CompressionService;
import gripe._90.megacells.service.DecompressionService;
import gripe._90.megacells.util.Addons;
import gripe._90.megacells.util.Utils;

public class MEGACells implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        initAll();
        registerAll();

        InitStorageCells.init();
        InitUpgrades.init();

        initCompression();
    }

    private void initAll() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGAParts.init();
        MEGABlockEntities.init();

        if (Utils.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            AppBotItems.init();
        }
    }

    private void registerAll() {
        for (var block : MEGABlocks.getBlocks()) {
            Registry.register(BuiltInRegistries.BLOCK, block.id(), block.block());
            Registry.register(BuiltInRegistries.ITEM, block.id(), block.asItem());
        }

        for (var item : MEGAItems.getItems()) {
            Registry.register(BuiltInRegistries.ITEM, item.id(), item.asItem());
        }

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MEGACreativeTab.ID, MEGACreativeTab.TAB);

        for (var blockEntity : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntity.getKey(), blockEntity.getValue());
        }

        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("mega_pattern_provider"),
                MEGAPatternProviderBlock.MENU);
    }

    private void initCompression() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> CompressionService.INSTANCE
                .loadRecipes(server.getRecipeManager(), server.registryAccess()));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
            }
        });

        GridServices.register(DecompressionService.class, DecompressionService.class);
        PatternDetailsHelper.registerDecoder(DecompressionPatternDecoder.INSTANCE);
    }
}

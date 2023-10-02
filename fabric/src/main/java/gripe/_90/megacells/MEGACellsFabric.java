package gripe._90.megacells;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.GridServices;
import appeng.core.AppEng;

import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.crafting.DecompressionPatternDecoder;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.service.CompressionService;
import gripe._90.megacells.service.DecompressionService;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        initAll();
        registerAll();

        InitStorageCells.init();
        InitUpgrades.init();

        initPatternProviderTransfer();
        initCompression();
    }

    private void initAll() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGAParts.init();
        MEGABlockEntities.init();

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            AppBotItems.init();
        }
    }

    private void registerAll() {
        for (var block : MEGABlocks.getBlocks()) {
            Registry.register(Registry.BLOCK, block.id(), block.block());
            Registry.register(Registry.ITEM, block.id(), block.asItem());
        }

        for (var item : MEGAItems.getItems()) {
            Registry.register(Registry.ITEM, item.id(), item.asItem());
        }

        for (var blockEntity : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, blockEntity.getKey(), blockEntity.getValue());
        }

        Registry.register(Registry.MENU, AppEng.makeId("mega_pattern_provider"), MEGAPatternProviderBlock.MENU);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initPatternProviderTransfer() {
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (be, context) -> be.getLogic().getReturnInv(), MEGABlockEntities.MEGA_PATTERN_PROVIDER);
    }

    private void initCompression() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> CompressionService.INSTANCE.loadRecipes(server.getRecipeManager()));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                CompressionService.INSTANCE.loadRecipes(server.getRecipeManager());
            }
        });

        GridServices.register(DecompressionService.class, DecompressionService.class);
        PatternDetailsHelper.registerDecoder(DecompressionPatternDecoder.INSTANCE);
    }
}

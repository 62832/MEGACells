package gripe._90.megacells;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.service.CompressionService;
import gripe._90.megacells.service.DecompressionService;

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

        /*
         * if (Utils.PLATFORM.isModLoaded("appbot")) { AppBotItems.init(); }
         */
    }

    private void registerAll() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MEGAItems.CREATIVE_TAB_ID, MEGAItems.CREATIVE_TAB);

        for (var block : MEGABlocks.getBlocks()) {
            Registry.register(BuiltInRegistries.BLOCK, block.id(), block.block());
            Registry.register(BuiltInRegistries.ITEM, block.id(), block.asItem());
        }

        for (var item : MEGAItems.getItems()) {
            Registry.register(BuiltInRegistries.ITEM, item.id(), item.asItem());
        }

        for (var blockEntity : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntity.getKey(), blockEntity.getValue());
        }

        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("mega_pattern_provider"),
                MEGAPatternProviderBlock.MENU);

        ItemGroupEvents.modifyEntriesEvent(MEGAItems.CREATIVE_TAB_KEY).register(content -> {
            MEGAItems.getItems().stream().filter(i -> i != MEGAItems.DECOMPRESSION_PATTERN).forEach(content::accept);
            MEGABlocks.getBlocks().forEach(content::accept);
        });
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

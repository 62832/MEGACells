package gripe._90.megacells.forge;

import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.api.networking.GridServices;
import appeng.core.AppEng;

import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.crafting.DecompressionService;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.util.CompressionService;

@Mod(gripe._90.megacells.MEGACells.MODID)
public class MEGACellsForge {
    public MEGACellsForge() {
        initAll();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerAll);
        bus.addListener(this::initCells);

        initCompression();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MEGACellsClient::new);
    }

    private void initAll() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGAParts.init();
        MEGABlockEntities.init();

        if (gripe._90.megacells.MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            AppMekItems.init();
        }

        if (gripe._90.megacells.MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            AppBotItems.init();
        }
    }

    private void registerAll(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registry.BLOCK_REGISTRY)) {
            MEGABlocks.getBlocks().forEach(b -> {
                ForgeRegistries.BLOCKS.register(b.id(), b.block());
                ForgeRegistries.ITEMS.register(b.id(), b.asItem());
            });
        }

        if (event.getRegistryKey().equals(Registry.ITEM_REGISTRY)) {
            MEGAItems.getItems().forEach(i -> ForgeRegistries.ITEMS.register(i.id(), i.asItem()));
        }

        if (event.getRegistryKey().equals(Registry.BLOCK_ENTITY_TYPE_REGISTRY)) {
            MEGABlockEntities.getBlockEntityTypes().forEach(ForgeRegistries.BLOCK_ENTITY_TYPES::register);
        }

        if (event.getRegistryKey().equals(Registry.MENU_REGISTRY)) {
            ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mega_pattern_provider"), MEGAPatternProviderBlock.MENU);
        }
    }

    private void initCells(FMLCommonSetupEvent event) {
        event.enqueueWork(InitStorageCells::init);
        event.enqueueWork(InitUpgrades::init);

        event.enqueueWork(() -> {
            if (gripe._90.megacells.MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
                AppMekIntegration.initUpgrades();
                AppMekIntegration.initStorageCells();
            }
        });
    }

    private void initCompression() {
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) ->
                CompressionService.INSTANCE.loadRecipes(event.getServer().getRecipeManager()));
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> CompressionService.INSTANCE.loadRecipes(
                event.getServerResources().getRecipeManager()));

        GridServices.register(DecompressionService.class, DecompressionService.class);
    }
}

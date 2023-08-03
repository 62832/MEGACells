package gripe._90.megacells.forge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.service.CompressionService;
import gripe._90.megacells.service.DecompressionService;
import gripe._90.megacells.util.Addons;
import gripe._90.megacells.util.Utils;

@Mod(Utils.MODID)
public class MEGACells {
    public MEGACells() {
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

        if (Utils.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            AppMekItems.init();
        }

        if (Utils.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            AppBotItems.init();
        }
    }

    private void registerAll(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            MEGABlocks.getBlocks().forEach(b -> {
                ForgeRegistries.BLOCKS.register(b.id(), b.block());
                ForgeRegistries.ITEMS.register(b.id(), b.asItem());
            });
        }

        if (event.getRegistryKey().equals(Registries.ITEM)) {
            MEGAItems.getItems().forEach(i -> ForgeRegistries.ITEMS.register(i.id(), i.asItem()));
        }

        if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MEGACreativeTab.ID, MEGACreativeTab.TAB);
        }

        if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            MEGABlockEntities.getBlockEntityTypes().forEach(ForgeRegistries.BLOCK_ENTITY_TYPES::register);
        }

        if (event.getRegistryKey().equals(Registries.MENU)) {
            ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mega_pattern_provider"), MEGAPatternProviderBlock.MENU);
        }
    }

    private void initCells(FMLCommonSetupEvent event) {
        event.enqueueWork(InitStorageCells::init);
        event.enqueueWork(InitUpgrades::init);

        event.enqueueWork(() -> {
            if (Utils.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
                AppMekIntegration.initUpgrades();
                AppMekIntegration.initStorageCells();
            }
        });
    }

    private void initCompression() {
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> CompressionService.INSTANCE.loadRecipes(
                event.getServer().getRecipeManager(), event.getServer().registryAccess()));
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> CompressionService.INSTANCE.loadRecipes(
                event.getServerResources().getRecipeManager(), event.getRegistryAccess()));

        GridServices.register(DecompressionService.class, DecompressionService.class);
        PatternDetailsHelper.registerDecoder(DecompressionPatternDecoder.INSTANCE);
    }
}

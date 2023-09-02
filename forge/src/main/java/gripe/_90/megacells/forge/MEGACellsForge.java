package gripe._90.megacells.forge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGACreativeTab;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

@Mod(MEGACells.MODID)
public class MEGACellsForge {
    public MEGACellsForge() {
        MEGACells.initCommon();

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            AppMekItems.init();
            AppMekIntegration.initStorageCells();
        }

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerAll);
        bus.addListener(this::initUpgrades);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            MEGACellsClient.init();
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
            ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mega_interface"), MEGAInterfaceMenu.TYPE);
            ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mega_pattern_provider"), MEGAPatternProviderMenu.TYPE);
        }
    }

    private void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(MEGACells::initUpgrades);

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            event.enqueueWork(AppMekIntegration::initUpgrades);
        }
    }
}

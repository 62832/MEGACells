package gripe._90.megacells.forge;

import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import gripe._90.megacells.datagen.forge.MEGADataGenerators;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.init.forge.client.InitAutoRotatingModel;
import gripe._90.megacells.init.forge.client.InitBlockEntityRenderers;
import gripe._90.megacells.init.forge.client.InitBuiltInModels;
import gripe._90.megacells.init.forge.client.InitItemColors;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.item.cell.CompressionHandler;
import gripe._90.megacells.util.Utils;

@Mod(Utils.MODID)
public class MEGACellsForge {
    public MEGACellsForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MEGABlocks.init();
        MEGAItems.init();
        MEGABlockEntities.init();

        if (Utils.PLATFORM.isModLoaded("appmek")) {
            AppMekItems.init();
        }

        if (Utils.PLATFORM.isModLoaded("appbot")) {
            AppBotItems.init();
        }

        bus.addListener((RegisterEvent event) -> {
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
        });

        bus.addListener(MEGADataGenerators::onGatherData);
        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(InitStorageCells::init);
            event.enqueueWork(InitUpgrades::init);

            event.enqueueWork(() -> {
                if (Utils.PLATFORM.isModLoaded("appmek")) {
                    AppMekIntegration.initUpgrades();
                    AppMekIntegration.initStorageCells();
                }
            });
        });

        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> CompressionHandler.INSTANCE.load());
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> CompressionHandler.INSTANCE.load());
        MinecraftForge.EVENT_BUS.addListener((RecipesUpdatedEvent event) -> CompressionHandler.INSTANCE.load());

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitAutoRotatingModel::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitBlockEntityRenderers::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitBuiltInModels::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitItemColors::init);
    }
}

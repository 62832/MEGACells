package gripe._90.megacells.forge;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.block.entity.MEGABlockEntities;
import gripe._90.megacells.datagen.forge.MEGADataGenerators;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.init.client.forge.InitAutoRotatingModel;
import gripe._90.megacells.init.client.forge.InitBlockEntityRenderers;
import gripe._90.megacells.init.client.forge.InitBuiltInModels;
import gripe._90.megacells.init.client.forge.InitItemColors;
import gripe._90.megacells.init.client.forge.InitRenderTypes;
import gripe._90.megacells.init.forge.Registration;
import gripe._90.megacells.integration.forge.appmek.item.AppMekItems;
import gripe._90.megacells.item.MEGAItems;

@Mod(MEGACells.MODID)
public class MEGACellsForge {

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MEGACells.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(MEGAItems.ITEM_CELL_256M);
        }
    };

    public MEGACellsForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MEGABlocks.init();
        MEGAItems.init();
        MEGABlockEntities.init();

        AppMekItems.init();

        bus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registry.BLOCK_REGISTRY)) {
                Registration.registerBlocks(ForgeRegistries.BLOCKS);
            }
            if (event.getRegistryKey().equals(Registry.ITEM_REGISTRY)) {
                Registration.registerItems(ForgeRegistries.ITEMS);
            }
            if (event.getRegistryKey().equals(Registry.BLOCK_ENTITY_TYPE_REGISTRY)) {
                Registration.registerBlockEntities(ForgeRegistries.BLOCK_ENTITY_TYPES);
            }
        });

        bus.addListener(MEGADataGenerators::onGatherData);
        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(InitStorageCells::init);
            event.enqueueWork(InitUpgrades::init);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitAutoRotatingModel::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitBlockEntityRenderers::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitBuiltInModels::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitItemColors::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InitRenderTypes::init);
    }
}

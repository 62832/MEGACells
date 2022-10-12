package gripe._90.megacells;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.block.entity.MEGABlockEntities;
import gripe._90.megacells.datagen.MEGADataGenerators;
import gripe._90.megacells.init.ae2.InitStorageCells;
import gripe._90.megacells.init.ae2.InitUpgrades;
import gripe._90.megacells.init.loader.Registration;
import gripe._90.megacells.init.loader.client.InitAutoRotatingModel;
import gripe._90.megacells.init.loader.client.InitBlockEntityRenderers;
import gripe._90.megacells.init.loader.client.InitBuiltInModels;
import gripe._90.megacells.init.loader.client.InitItemColors;
import gripe._90.megacells.init.loader.client.InitRenderTypes;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.item.MEGAItems;

@Mod(MEGACells.MODID)
public class MEGACells {

    public static final String MODID = "megacells";

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MEGACells.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(MEGAItems.ITEM_CELL_256M);
        }
    };

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MEGACells.MODID, path);
    }

    public static String getItemPath(Item item) {
        return Objects.requireNonNull(Registry.ITEM.getKey(item)).getPath();
    }

    public MEGACells() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MEGABlocks.init();
        MEGAItems.init();
        MEGABlockEntities.init();

        AppMekItems.init();

        bus.addListener((RegisterEvent event) -> {
            Registration.initBlocks(ForgeRegistries.BLOCKS);
            Registration.initItems(ForgeRegistries.ITEMS);
            Registration.initBlockEntities(ForgeRegistries.BLOCK_ENTITY_TYPES);
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

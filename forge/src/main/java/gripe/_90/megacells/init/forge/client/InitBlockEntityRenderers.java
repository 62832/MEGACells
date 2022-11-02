package gripe._90.megacells.init.forge.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.client.render.crafting.CraftingMonitorRenderer;

import gripe._90.megacells.block.entity.MEGABlockEntities;

public class InitBlockEntityRenderers {

    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitBlockEntityRenderers::initBERenderers);
    }

    private static void initBERenderers(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);
    }
}

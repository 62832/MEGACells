package gripe._90.megacells.init.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import gripe._90.megacells.block.entity.MEGABlockEntities;

import appeng.client.render.crafting.CraftingMonitorRenderer;

@OnlyIn(Dist.CLIENT)
public class InitBlockEntityRenderers {

    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitBlockEntityRenderers::initBERenderers);
    }

    private static void initBERenderers(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);
    }
}

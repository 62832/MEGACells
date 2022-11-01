package gripe._90.megacells.init.client.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

import appeng.client.render.crafting.CraftingMonitorRenderer;

import gripe._90.megacells.block.entity.MEGABlockEntities;

@Environment(EnvType.CLIENT)
public class InitBlockEntityRenderers {
    public static void init() {
        BlockEntityRendererRegistry.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);
    }
}

package gripe._90.megacells.client.render;

import org.joml.Vector3f;

import net.minecraft.client.renderer.item.ItemStackRenderState;

import appeng.api.orientation.BlockOrientation;
import appeng.client.api.renderer.parts.PartDynamicRenderState;

public class CellDockRenderState extends PartDynamicRenderState {
    public final ItemStackRenderState cell = new ItemStackRenderState();
    public BlockOrientation orientation;
    public Vector3f ledColor;
}

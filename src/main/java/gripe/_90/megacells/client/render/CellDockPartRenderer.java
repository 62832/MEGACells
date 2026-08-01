package gripe._90.megacells.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import appeng.api.orientation.BlockOrientation;
import appeng.api.storage.cells.CellState;
import appeng.client.api.renderer.parts.PartRenderer;
import appeng.client.render.AERenderTypes;
import appeng.client.renderer.blockentity.CellLedRenderer;

import gripe._90.megacells.item.part.CellDockPart;

public class CellDockPartRenderer implements PartRenderer<CellDockPart, CellDockRenderState> {
    private final ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();

    @Override
    public CellDockRenderState createState() {
        return new CellDockRenderState();
    }

    @Override
    public Class<CellDockRenderState> stateClass() {
        return CellDockRenderState.class;
    }

    @Override
    public void extract(CellDockPart part, CellDockRenderState state, float partialTicks) {
        state.orientation = BlockOrientation.get(part.getSide(), part.getSpin());
        state.cell.clear();

        var cellItem = part.getCellItem(0);
        if (cellItem != null) {
            itemModelResolver.updateForTopItem(
                    state.cell, new ItemStack(cellItem), ItemDisplayContext.FIXED, part.getLevel(), null, (int)
                            part.getBlockEntity().getBlockPos().asLong());
        }

        state.ledColor = getLedColor(part.getCellStatus(0), part.isPowered());
    }

    @Override
    public void submit(
            CellDockRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector nodes,
            CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(state.orientation.getQuaternion());

        if (!state.cell.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.47);
            poseStack.scale(0.42f, 0.42f, 0.42f);
            state.cell.submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (state.ledColor != null) {
            poseStack.pushPose();
            poseStack.translate(-5 / 16f, -5 / 16f, 7.01f / 16f);
            nodes.submitCustomGeometry(
                    poseStack,
                    AERenderTypes.STORAGE_CELL_LEDS,
                    (pose, consumer) -> CellLedRenderer.renderLed(state.ledColor, consumer, pose));
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static Vector3f getLedColor(CellState state, boolean powered) {
        if (state == CellState.ABSENT) {
            return null;
        }
        if (!powered) {
            return new Vector3f();
        }

        var color = state.getStateColor();
        return new Vector3f(((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
    }
}

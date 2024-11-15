package gripe._90.megacells.client.render;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import appeng.api.orientation.BlockOrientation;
import appeng.client.render.DelegateBakedModel;

/**
 * Temporary copy of {@link appeng.client.render.tesr.ChestBlockEntityRenderer.FaceRotatingModel}
 */
public class FaceRotatingModel extends DelegateBakedModel {
    private final BlockOrientation orientation;

    public FaceRotatingModel(BakedModel base, BlockOrientation orientation) {
        super(base);
        this.orientation = orientation;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            @NotNull RandomSource rand,
            @NotNull ModelData extraData,
            RenderType renderType) {
        if (side != null) {
            side = orientation.resultingRotate(side);
        }

        var quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));

        for (int i = 0; i < quads.size(); i++) {
            var quad = quads.get(i);
            quads.set(
                    i,
                    new BakedQuad(
                            quad.getVertices(),
                            quad.getTintIndex(),
                            orientation.rotate(quad.getDirection()),
                            quad.getSprite(),
                            quad.isShade()));
        }

        return quads;
    }
}

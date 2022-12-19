package gripe._90.megacells.mixin.forge;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.ModelData;

import appeng.client.render.crafting.LightBakedModel;
import appeng.client.render.crafting.MonitorBakedModel;
import appeng.client.render.crafting.UnitBakedModel;

@Mixin({ UnitBakedModel.class, LightBakedModel.class, MonitorBakedModel.class })
public abstract class CraftingCubeBakedModelMixin implements IForgeBakedModel {
    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
            @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }
}

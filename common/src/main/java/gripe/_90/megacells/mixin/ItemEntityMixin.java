package gripe._90.megacells.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import gripe._90.megacells.misc.LavaTransformLogic;

@Mixin(value = ItemEntity.class, priority = 500)
public abstract class ItemEntityMixin extends Entity {
    @SuppressWarnings("unused")
    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Unique
    private boolean mega$lavaImmune;

    @Unique
    private int mega$lavaTicks;

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    private void handleLavaTransform(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || mega$lavaImmune);
    }

    @SuppressWarnings("resource")
    @Inject(method = "tick", at = @At("RETURN"))
    private void lavaTimeout(CallbackInfo ci) {
        var self = (ItemEntity) (Object) this;

        if (LavaTransformLogic.canTransformInLava(self)) {
            var x = Mth.floor(getX());
            var y = Mth.floor((getBoundingBox().minY + getBoundingBox().maxY) / 2);
            var z = Mth.floor(getZ());
            var state = level().getFluidState(new BlockPos(x, y, z));

            if (state.is(FluidTags.LAVA)) {
                mega$lavaImmune = mega$lavaTicks++ <= 200 || LavaTransformLogic.allIngredientsPresent(self);

                if (mega$lavaTicks > 200 && LavaTransformLogic.allIngredientsPresent(self)) {
                    mega$lavaTicks = 0;
                }
            }
        }
    }
}

package gripe._90.megacells.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.resources.ResourceLocation;

import appeng.hooks.BuiltInModelHooks;

import gripe._90.megacells.MEGACells;

@Mixin(BuiltInModelHooks.class)
public abstract class BuiltInModelHooksMixin {
    @ModifyExpressionValue(
            method = "getBuiltInModel",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private static boolean isMEGAModel(boolean original, @Local(argsOnly = true) ResourceLocation variantId) {
        return original || MEGACells.MODID.equals(variantId.getNamespace());
    }
}

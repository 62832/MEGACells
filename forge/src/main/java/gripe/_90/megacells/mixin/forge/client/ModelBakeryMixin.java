package gripe._90.megacells.mixin.forge.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import gripe._90.megacells.MEGACells;

/**
 * Replicates AE2's own {@link appeng.mixins.ModelBakeryMixin} for models from the MEGA namespace.
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void loadModelHook(ResourceLocation id, CallbackInfo ci) {
        var model = megacells$getUnbakedModel(id);

        if (model != null) {
            cacheAndQueueDependencies(id, model);
            ci.cancel();
        }
    }

    @Unique
    private UnbakedModel megacells$getUnbakedModel(ResourceLocation variantId) {
        if (!variantId.getNamespace().equals(MEGACells.MODID)) {
            return null;
        }

        if (variantId instanceof ModelResourceLocation modelId) {
            if ("inventory".equals(modelId.getVariant())) {
                var itemModelId = new ResourceLocation(modelId.getNamespace(), "item/" + modelId.getPath());
                return BuiltInModelHooksAccessor.getBuiltInModels().get(itemModelId);
            }

            return null;
        } else {
            return BuiltInModelHooksAccessor.getBuiltInModels().get(variantId);
        }
    }

    @Shadow
    protected abstract void cacheAndQueueDependencies(ResourceLocation id, UnbakedModel unbakedModel);
}

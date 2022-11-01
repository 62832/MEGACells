package gripe._90.megacells.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.mari_023.ae2wtlib.AE2wtlibEntrypoint;

import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;

@Mixin(AE2wtlibEntrypoint.class)
@Pseudo
public class AE2WTLibEntrypointMixin {
    @Inject(method = "onAe2Initialized", at = @At("TAIL"), remap = false)
    public void initIntegration(CallbackInfo ci) {
        AE2WTIntegration.initIntegration();
    }
}

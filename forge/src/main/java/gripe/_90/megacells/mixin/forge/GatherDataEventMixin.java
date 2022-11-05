package gripe._90.megacells.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Temporary dirty fix for the <code>:forge:runData</code> Gradle task hanging without exiting on Architectury.
 */
@Mixin(targets = "net/minecraftforge/data/event/GatherDataEvent$DataGeneratorConfig")
public class GatherDataEventMixin {
    @Inject(method = "runAll", at = @At("TAIL"), remap = false)
    private void exit(CallbackInfo ci) {
        System.exit(0);
    }
}

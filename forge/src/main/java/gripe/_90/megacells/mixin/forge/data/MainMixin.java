package gripe._90.megacells.mixin.forge.data;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.data.Main;

/**
 * Temporary dirty fix for the <code>:forge:runData</code> Gradle task hanging without exiting on Architectury.
 */
@Mixin(Main.class)
public class MainMixin {
    @Inject(method = "main", at = @At("TAIL"), remap = false)
    private static void exit(String[] strings, CallbackInfo ci) {
        System.exit(0);
    }
}

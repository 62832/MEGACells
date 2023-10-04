package gripe._90.megacells.mixin.data;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.data.models.model.TextureSlot;

@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {
    @Invoker
    static TextureSlot invokeCreate(String string) {
        throw new AssertionError();
    }
}

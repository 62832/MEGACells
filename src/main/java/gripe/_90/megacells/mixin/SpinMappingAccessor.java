package gripe._90.megacells.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Direction;

import appeng.block.orientation.SpinMapping;

@Mixin(SpinMapping.class)
public interface SpinMappingAccessor {
    @Accessor(value = "SPIN_DIRECTIONS")
    static Direction[][] getSpinDirections() {
        throw new AssertionError();
    }
}

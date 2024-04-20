package gripe._90.megacells.misc;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

import gripe._90.megacells.definition.MEGAItems;

public class DecompressionPattern implements IPatternDetails {
    static final String NBT_BASE = "base";
    static final String NBT_VARIANT = "variant";
    static final String NBT_FACTOR = "factor";

    private final AEItemKey definition;
    private final AEItemKey base;
    private final AEItemKey variant;
    private final byte factor;

    public DecompressionPattern(AEItemKey definition) {
        this.definition = definition;

        var tag = Objects.requireNonNull(definition.getTag());
        base = AEItemKey.fromTag(tag.getCompound(NBT_BASE));
        variant = AEItemKey.fromTag(tag.getCompound(NBT_VARIANT));
        factor = tag.getByte(NBT_FACTOR);
    }

    public DecompressionPattern(AEItemKey base, CompressionService.Variant variant) {
        this.base = base;
        this.variant = variant.item();
        this.factor = variant.factor();

        var tag = new CompoundTag();
        tag.put(NBT_BASE, this.base.toTag());
        tag.put(NBT_VARIANT, this.variant.toTag());
        tag.putByte(NBT_FACTOR, this.factor);

        definition = AEItemKey.of(MEGAItems.DECOMPRESSION_PATTERN, tag);
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {new Input(variant)};
    }

    @Override
    public GenericStack[] getOutputs() {
        return new GenericStack[] {new GenericStack(base, factor)};
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj.getClass() == getClass()
                && ((DecompressionPattern) obj).definition.equals(definition);
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

    private record Input(AEItemKey input) implements IInput {
        @Override
        public GenericStack[] getPossibleInputs() {
            return new GenericStack[] {new GenericStack(input, 1)};
        }

        @Override
        public long getMultiplier() {
            return 1;
        }

        @Override
        public boolean isValid(AEKey input, Level level) {
            return input.matches(getPossibleInputs()[0]);
        }

        @Override
        public AEKey getRemainingKey(AEKey template) {
            return null;
        }
    }
}

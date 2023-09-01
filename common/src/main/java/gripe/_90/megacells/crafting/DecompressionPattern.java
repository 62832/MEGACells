package gripe._90.megacells.crafting;

import java.util.Objects;

import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public class DecompressionPattern implements IPatternDetails {
    private final AEItemKey definition;
    private final AEItemKey compressed;
    private final long count;
    private final AEItemKey decompressed;
    private final int factor;

    public DecompressionPattern(AEItemKey definition) {
        this.definition = definition;
        var tag = Objects.requireNonNull(definition.getTag());

        this.compressed = DecompressionPatternEncoding.getCompressed(tag);
        this.count = DecompressionPatternEncoding.getCount(tag);

        this.decompressed = DecompressionPatternEncoding.getDecompressed(tag);
        this.factor = DecompressionPatternEncoding.getFactor(tag);
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {new Input(compressed, count)};
    }

    @Override
    public GenericStack[] getOutputs() {
        return new GenericStack[] {new GenericStack(decompressed, count * factor)};
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

    private record Input(AEItemKey input, long multiplier) implements IInput {
        @Override
        public GenericStack[] getPossibleInputs() {
            return new GenericStack[] {new GenericStack(input, 1)};
        }

        @Override
        public long getMultiplier() {
            return multiplier;
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

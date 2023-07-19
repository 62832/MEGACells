package gripe._90.megacells.crafting;

import java.util.Objects;

import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.items.AEBaseItem;

public class MEGADecompressionPattern implements IPatternDetails {
    private final AEItemKey definition;
    private final AEItemKey input;
    private final IInput[] inputs;
    private final GenericStack[] outputs;

    public MEGADecompressionPattern(AEItemKey definition) {
        this.definition = definition;
        var tag = Objects.requireNonNull(definition.getTag());

        this.input = DecompressionPatternEncoding.getCompressed(tag);

        var decompressed = DecompressionPatternEncoding.getDecompressed(tag);
        var factor = DecompressionPatternEncoding.getFactor(tag);
        var output = decompressed.toStack(factor);

        this.inputs = new IInput[] { new Input() };
        this.outputs = new GenericStack[] { GenericStack.fromItemStack(output) };
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return inputs;
    }

    @Override
    public GenericStack[] getOutputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass()
                && ((MEGADecompressionPattern) obj).definition.equals(definition);
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

    private class Input implements IInput {
        @Override
        public GenericStack[] getPossibleInputs() {
            return new GenericStack[] { new GenericStack(input, 1) };
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

    public static class Item extends AEBaseItem {
        public Item(Properties properties) {
            super(properties);
        }
    }
}

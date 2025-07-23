package gripe._90.megacells.misc;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;

public class DecompressionPattern implements IPatternDetails {
    private final AEItemKey definition;
    private final ItemStack from;
    private final ItemStack to;

    public DecompressionPattern(ItemStack from, ItemStack to) {
        this.from = from;
        this.to = to;

        var definition = new ItemStack(MEGAItems.SKY_STEEL_INGOT);
        definition.set(MEGAComponents.ENCODED_DECOMPRESSION_PATTERN, new Encoded(from, to));
        this.definition = AEItemKey.of(definition);
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {new Input(from)};
    }

    @Override
    public List<GenericStack> getOutputs() {
        return Collections.singletonList(new GenericStack(AEItemKey.of(to), to.getCount()));
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass() && ((DecompressionPattern) o).definition.equals(definition);
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

    private record Input(ItemStack stack) implements IInput {
        @Override
        public GenericStack[] getPossibleInputs() {
            return new GenericStack[] {new GenericStack(AEItemKey.of(stack), stack.getCount())};
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

    public record Encoded(ItemStack from, ItemStack to) {
        public static final Codec<Encoded> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ItemStack.CODEC.fieldOf("from").forGetter(Encoded::from),
                        ItemStack.CODEC.fieldOf("to").forGetter(Encoded::to))
                .apply(instance, Encoded::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Encoded> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Encoded::from, ItemStack.STREAM_CODEC, Encoded::to, Encoded::new);
    }
}

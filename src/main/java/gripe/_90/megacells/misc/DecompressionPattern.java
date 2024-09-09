package gripe._90.megacells.misc;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;

public class DecompressionPattern implements IPatternDetails {
    private final AEItemKey definition;
    private final AEItemKey base;
    private final AEItemKey variant;
    private final byte factor;

    public DecompressionPattern(AEItemKey definition) {
        this.definition = definition;
        var encodedPattern = definition.get(MEGAComponents.ENCODED_DECOMPRESSION_PATTERN);

        if (encodedPattern == null) {
            throw new IllegalArgumentException("Given item does not encode a decompression pattern: " + definition);
        } else if (encodedPattern.containsMissingContent()) {
            throw new IllegalArgumentException("Pattern references missing content");
        }

        base = AEItemKey.of(encodedPattern.base);
        variant = AEItemKey.of(encodedPattern.variant);
        factor = encodedPattern.factor;
    }

    public DecompressionPattern(AEItemKey base, CompressionService.Variant variant) {
        this.base = base;
        this.variant = variant.item();
        this.factor = variant.factor();

        var definition = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
        definition.set(
                MEGAComponents.ENCODED_DECOMPRESSION_PATTERN,
                new Encoded(base.toStack(), variant.item().toStack(), variant.factor()));
        this.definition = AEItemKey.of(definition);
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
    public List<GenericStack> getOutputs() {
        return List.of(new GenericStack(base, factor));
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

    public record Encoded(ItemStack base, ItemStack variant, byte factor) {
        public static final Codec<Encoded> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ItemStack.CODEC.fieldOf("base").forGetter(Encoded::base),
                        ItemStack.CODEC.fieldOf("variant").forGetter(Encoded::variant),
                        Codec.BYTE.fieldOf("factor").forGetter(Encoded::factor))
                .apply(builder, Encoded::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Encoded> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                Encoded::base,
                ItemStack.STREAM_CODEC,
                Encoded::variant,
                ByteBufCodecs.BYTE,
                Encoded::factor,
                Encoded::new);

        private boolean containsMissingContent() {
            return AEItems.MISSING_CONTENT.is(base) || AEItems.MISSING_CONTENT.is(variant);
        }
    }
}

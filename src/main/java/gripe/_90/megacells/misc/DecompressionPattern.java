package gripe._90.megacells.misc;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
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
    private final Item base;
    private final Item variant;
    private final int factor;
    private final boolean toCompress;

    public DecompressionPattern(Item base, Item variant, int factor, boolean toCompress) {
        this.base = base;
        this.variant = variant;
        this.factor = factor;
        this.toCompress = toCompress;

        var definition = new ItemStack(MEGAItems.SKY_STEEL_INGOT);
        definition.set(MEGAComponents.ENCODED_DECOMPRESSION_PATTERN, new Encoded(base, variant, factor, toCompress));
        this.definition = AEItemKey.of(definition);
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {toCompress ? new Input(base, factor) : new Input(variant, 1)};
    }

    @Override
    public List<GenericStack> getOutputs() {
        return Collections.singletonList(
                toCompress ? new GenericStack(AEItemKey.of(variant), 1) : new GenericStack(AEItemKey.of(base), factor));
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass() && ((DecompressionPattern) o).definition.equals(definition);
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

    private record Input(Item input, int factor) implements IInput {
        @Override
        public GenericStack[] getPossibleInputs() {
            return new GenericStack[] {new GenericStack(AEItemKey.of(input), 1)};
        }

        @Override
        public long getMultiplier() {
            return factor;
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

    public record Encoded(Item base, Item variant, int factor, boolean toCompress) {
        public static final Codec<Encoded> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("base").forGetter(Encoded::base),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("variant").forGetter(Encoded::variant),
                        Codec.INT.fieldOf("factor").forGetter(Encoded::factor),
                        Codec.BOOL.fieldOf("toCompress").forGetter(Encoded::toCompress))
                .apply(instance, Encoded::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Encoded> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM),
                Encoded::base,
                ByteBufCodecs.registry(Registries.ITEM),
                Encoded::variant,
                ByteBufCodecs.VAR_INT,
                Encoded::factor,
                ByteBufCodecs.BOOL,
                Encoded::toCompress,
                Encoded::new);
    }
}

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
    private final Item from;
    private final Item to;
    private final int factor;
    private final boolean compress;

    public DecompressionPattern(Item from, Item to, int factor, boolean compress) {
        this.from = from;
        this.to = to;
        this.factor = factor;
        this.compress = compress;

        var definition = new ItemStack(MEGAItems.SKY_STEEL_INGOT);
        definition.set(MEGAComponents.ENCODED_DECOMPRESSION_PATTERN, new Encoded(from, to, factor, compress));
        this.definition = AEItemKey.of(definition);
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {new Input(from, compress ? factor : 1)};
    }

    @Override
    public List<GenericStack> getOutputs() {
        return Collections.singletonList(new GenericStack(AEItemKey.of(to), compress ? 1 : factor));
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
            return new GenericStack[] {new GenericStack(AEItemKey.of(input), factor)};
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

    public record Encoded(Item from, Item to, int factor, boolean compress) {
        public static final Codec<Encoded> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("from").forGetter(Encoded::from),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("to").forGetter(Encoded::to),
                        Codec.INT.fieldOf("factor").forGetter(Encoded::factor),
                        Codec.BOOL.fieldOf("compress").forGetter(Encoded::compress))
                .apply(instance, Encoded::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Encoded> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM),
                Encoded::from,
                ByteBufCodecs.registry(Registries.ITEM),
                Encoded::to,
                ByteBufCodecs.VAR_INT,
                Encoded::factor,
                ByteBufCodecs.BOOL,
                Encoded::compress,
                Encoded::new);
    }
}

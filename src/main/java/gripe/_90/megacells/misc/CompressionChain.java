package gripe._90.megacells.misc;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEItemKey;

public class CompressionChain {
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressionChain> STREAM_CODEC =
            Variant.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CompressionChain::new, chain -> chain.variants);

    private final List<Variant> variants;

    CompressionChain(List<Variant> variants) {
        this.variants = Collections.unmodifiableList(variants);
    }

    public boolean isEmpty() {
        return variants.isEmpty();
    }

    public boolean containsVariant(Item item) {
        for (var variant : variants) {
            if (variant.item().equals(item)) {
                return true;
            }
        }

        return false;
    }

    public Variant get(int index) {
        return variants.get(index);
    }

    public BigInteger unitFactor(AEItemKey item) {
        if (item == null) {
            return BigInteger.ONE;
        }

        for (var variant : variants) {
            if (variant.item().equals(item.getItem())) {
                return limited(variants.indexOf(variant) + 1).stream()
                        .map(v -> BigInteger.valueOf(v.factor()))
                        .reduce(BigInteger.ONE, BigInteger::multiply);
            }
        }

        return BigInteger.ONE;
    }

    public List<Variant> limited(int limit) {
        return variants.subList(0, limit);
    }

    public List<Variant> trailing(int start) {
        return variants.subList(start, variants.size());
    }

    public int size() {
        return variants.size();
    }

    public List<Variant> lastMultiplierSwapped(int cutoff) {
        var subChain = limited(cutoff);
        var multipliers = subChain.stream().map(Variant::factor).collect(Collectors.toList());
        Collections.rotate(multipliers, -1);

        var items = subChain.stream().map(Variant::item).toList();
        var swapped = new ObjectArrayList<Variant>();

        for (var i = 0; i < items.size(); i++) {
            swapped.add(new Variant(items.get(i), multipliers.get(i)));
        }

        return Collections.unmodifiableList(swapped);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CompressionChain chain && variants.equals(chain.variants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variants);
    }

    public record Variant(Item item, int factor) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Variant> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM),
                Variant::item,
                ByteBufCodecs.VAR_INT,
                Variant::factor,
                Variant::new);

        @Override
        public String toString() {
            return factor + "x → " + item;
        }
    }
}

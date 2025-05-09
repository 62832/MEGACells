package gripe._90.megacells.misc;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.Lazy;

import appeng.api.crafting.IPatternDetails;

public class CompressionChain {
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressionChain> STREAM_CODEC =
            Variant.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CompressionChain::new, chain -> chain.variants);

    public static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final List<Variant> variants;
    private final Lazy<List<Pair<IPatternDetails, IPatternDetails>>> patterns = Lazy.of(this::gatherPatterns);

    CompressionChain(List<Variant> variants) {
        this.variants = Collections.unmodifiableList(variants);
    }

    public static long clamp(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    public boolean isEmpty() {
        return variants.isEmpty();
    }

    public boolean containsVariant(Item item) {
        for (var variant : variants) {
            if (variant.item.equals(item)) {
                return true;
            }
        }

        return false;
    }

    public Item getItem(int index) {
        return variants.get(index).item;
    }

    public BigInteger unitFactor(Item item) {
        if (item == null) {
            return BigInteger.ONE;
        }

        var potentialFactor = BigInteger.ONE;

        for (var variant : variants) {
            potentialFactor = potentialFactor.multiply(variant.big());

            if (variant.item.equals(item)) {
                return potentialFactor;
            }
        }

        return BigInteger.ONE;
    }

    public int size() {
        return variants.size();
    }

    public List<IPatternDetails> getDecompressionPatterns(int cutoff) {
        if (isEmpty()) {
            return List.of();
        }

        var decompressionPatterns = new ObjectArrayList<IPatternDetails>();
        var availablePatterns = patterns.get();

        for (var i = 0; i < variants.subList(0, cutoff).size(); i++) {
            decompressionPatterns.add(availablePatterns.get(i).right());
        }

        for (var i = cutoff; i < variants.size() - 1; i++) {
            decompressionPatterns.add(availablePatterns.get(i).left());
        }

        return Collections.unmodifiableList(decompressionPatterns);
    }

    private List<Pair<IPatternDetails, IPatternDetails>> gatherPatterns() {
        var patterns = new ObjectArrayList<Pair<IPatternDetails, IPatternDetails>>();

        for (var i = 0; i < variants.size() - 1; i++) {
            var smaller = variants.get(i);
            var larger = variants.get(i + 1);

            var compression = new DecompressionPattern(smaller.item, larger.item, larger.factor, true);
            var decompression = new DecompressionPattern(larger.item, smaller.item, larger.factor, false);

            patterns.add(Pair.of(compression, decompression));
        }

        return patterns;
    }

    public Map<Item, Long> initStacks(BigInteger unitCount, int cutoff, Item fallback) {
        var stacks = new Object2LongLinkedOpenHashMap<Item>();

        if (isEmpty()) {
            if (fallback != null) {
                stacks.put(fallback, clamp(unitCount, STACK_LIMIT));
            }

            return stacks;
        }

        for (var i = 0; i < cutoff + 1; i++) {
            var variant = variants.get(i);

            if (i < cutoff) {
                // use factor of the next variant along (determines how many of *this* variant fit into the next)
                var factor = variants.get((i + 1) % variants.size()).big();
                stacks.put(variant.item(), unitCount.remainder(factor).longValue());
                unitCount = unitCount.divide(factor);
            } else {
                stacks.put(variant.item(), clamp(unitCount, STACK_LIMIT));
                break;
            }
        }

        return stacks;
    }

    public void updateStacks(Map<Item, Long> stackMap, BigInteger unitsToAdd, int cutoff) {
        if (isEmpty()) {
            if (stackMap.size() > 1) {
                throw new IllegalStateException("Bulk cell reported more than one stack for empty compression chain");
            }

            if (!stackMap.isEmpty()) {
                var item = stackMap.keySet().iterator().next();
                var amount = BigInteger.valueOf(stackMap.get(item));
                stackMap.put(item, clamp(amount.add(unitsToAdd), STACK_LIMIT));
            }

            return;
        }

        for (var i = 0; i < cutoff + 1; i++) {
            var variant = variants.get(i);
            var amount = BigInteger.valueOf(stackMap.get(variant.item));

            if (unitsToAdd.signum() != 0 && i < cutoff) {
                // use factor of the next variant along (determines how many of *this* variant fit into the next)
                var factor = variants.get((i + 1) % variants.size()).big();

                var added = unitsToAdd.remainder(factor);
                amount = amount.add(added);
                unitsToAdd = unitsToAdd.subtract(added);

                if (amount.signum() == -1 || amount.divide(factor).signum() == 1) {
                    var outflow = amount.add(factor).remainder(factor);
                    unitsToAdd = unitsToAdd.add(amount.subtract(outflow));
                    amount = outflow;
                }

                stackMap.put(variant.item, amount.longValue());
                unitsToAdd = unitsToAdd.divide(factor);
            } else {
                stackMap.put(variant.item, clamp(amount.add(unitsToAdd), STACK_LIMIT));
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass() && ((CompressionChain) o).variants.equals(variants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variants);
    }

    record Variant(Item item, int factor) {
        private static final StreamCodec<RegistryFriendlyByteBuf, Variant> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM),
                Variant::item,
                ByteBufCodecs.VAR_INT,
                Variant::factor,
                Variant::new);

        private BigInteger big() {
            return BigInteger.valueOf(factor);
        }

        @NotNull
        @Override
        public String toString() {
            return factor + "x → " + item;
        }
    }
}

package gripe._90.megacells.misc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import appeng.api.crafting.IPatternDetails;

public class CompressionChain {
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressionChain> STREAM_CODEC =
            Variant.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CompressionChain::new, chain -> chain.variants);

    public static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final List<Variant> variants;
    private List<Pair<IPatternDetails, IPatternDetails>> patterns;

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

    public Item getCutoffItem(int cutoff) {
        return variants.get(cutoff - 1).item;
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

        // lazily initialise full pattern list if it hasn't been already
        if (patterns == null) {
            patterns = new ObjectArrayList<>();

            for (var i = 1; i < variants.size(); i++) {
                var current = variants.get(i);
                var previous = variants.get(i - 1);

                var compression = new DecompressionPattern(previous.item, current.item, current.factor, true);
                var decompression = new DecompressionPattern(current.item, previous.item, current.factor, false);

                patterns.add(Pair.of(compression, decompression));
            }
        }

        var decompressionPatterns = new ObjectArrayList<IPatternDetails>();
        var decompressionChain = variants.subList(0, cutoff).reversed();

        for (var i = 0; i < decompressionChain.size() - 1; i++) {
            decompressionPatterns.add(patterns.get(patterns.size() - i - 1).right());
        }

        var remainingChain = variants.subList(decompressionChain.size() - 1, variants.size());

        for (var i = 1; i < remainingChain.size(); i++) {
            decompressionPatterns.add(patterns.get(i).left());
        }

        return Collections.unmodifiableList(decompressionPatterns);
    }

    public Map<Item, Long> initStacks(BigInteger unitCount, int cutoff, Item fallback) {
        var stacks = new Object2LongLinkedOpenHashMap<Item>();

        if (isEmpty()) {
            if (fallback != null) {
                stacks.put(fallback, clamp(unitCount, STACK_LIMIT));
            }

            return stacks;
        }

        var swapped = lastMultiplierSwapped(cutoff);

        for (var variant : swapped) {
            if (variant != swapped.getLast()) {
                var factor = variant.big();
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

        var swapped = lastMultiplierSwapped(cutoff);

        for (var variant : lastMultiplierSwapped(cutoff)) {
            var factor = variant.big();
            var amount = BigInteger.valueOf(stackMap.get(variant.item));

            if (unitsToAdd.divide(factor).signum() != 0 && variant != swapped.getLast()) {
                var added = unitsToAdd.remainder(factor);
                amount = amount.add(added);
                unitsToAdd = unitsToAdd.subtract(added);

                if (amount.signum() == -1 || amount.divide(factor).signum() == 1) {
                    var outflow = amount.remainder(factor);
                    amount = amount.subtract(outflow);
                    unitsToAdd = unitsToAdd.add(outflow);
                }

                stackMap.put(variant.item, amount.longValue());
                unitsToAdd = unitsToAdd.divide(factor);
            } else {
                stackMap.put(variant.item, clamp(amount.add(unitsToAdd), STACK_LIMIT));
                break;
            }
        }
    }

    private List<Variant> lastMultiplierSwapped(int cutoff) {
        var subChain = variants.subList(0, cutoff);

        if (subChain.isEmpty()) {
            return subChain;
        }

        var swapped = new ArrayList<Variant>();

        for (var i = 1; i < subChain.size(); i++) {
            swapped.add(new Variant(subChain.get(i - 1).item, subChain.get(i).factor));
        }

        swapped.add(new Variant(subChain.getLast().item, subChain.getFirst().factor));
        return swapped;
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

        @Override
        public String toString() {
            return factor + "x → " + item;
        }
    }
}

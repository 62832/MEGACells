package gripe._90.megacells.misc;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;

public class CompressionChain {
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressionChain> STREAM_CODEC =
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CompressionChain::new, chain -> chain.variants);

    public static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final List<ItemStack> variants;
    private final Supplier<List<Pair<IPatternDetails, IPatternDetails>>> patterns = Lazy.of(this::gatherPatterns);

    CompressionChain(List<ItemStack> variants) {
        this.variants = Collections.unmodifiableList(variants);
    }

    public static long clamp(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    private static BigInteger bigCount(ItemStack stack) {
        return BigInteger.valueOf(stack.getCount());
    }

    public boolean isEmpty() {
        return variants.isEmpty();
    }

    public boolean containsVariant(AEItemKey item) {
        for (var variant : variants) {
            if (ItemStack.isSameItemSameComponents(item.getReadOnlyStack(), variant)) {
                return true;
            }
        }

        return false;
    }

    public ItemStack getItem(int index) {
        return variants.get(index).copy();
    }

    public BigInteger unitFactor(AEItemKey item) {
        if (item == null) {
            return BigInteger.ONE;
        }

        var potentialFactor = BigInteger.ONE;

        for (var variant : variants) {
            potentialFactor = potentialFactor.multiply(bigCount(variant));

            if (ItemStack.isSameItemSameComponents(item.getReadOnlyStack(), variant)) {
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
            var smaller = variants.get(i).copyWithCount(variants.get(i + 1).getCount());
            var larger = variants.get(i + 1).copyWithCount(1);

            var compression = new DecompressionPattern(smaller, larger);
            var decompression = new DecompressionPattern(larger, smaller);

            patterns.add(Pair.of(compression, decompression));
        }

        return patterns;
    }

    public Map<AEItemKey, Long> initStacks(BigInteger unitCount, int cutoff, AEItemKey fallback) {
        var stacks = new Object2LongLinkedOpenHashMap<AEItemKey>();

        if (!isEmpty()) {
            for (var i = 0; i < cutoff + 1; i++) {
                var variant = AEItemKey.of(variants.get(i));

                if (i < cutoff) {
                    // use factor of the next variant along (determines how many of *this* variant fit into the next)
                    var factor = bigCount(variants.get((i + 1) % variants.size()));
                    stacks.put(variant, unitCount.remainder(factor).longValue());
                    unitCount = unitCount.divide(factor);
                } else {
                    stacks.put(variant, clamp(unitCount, STACK_LIMIT));
                    break;
                }
            }
        } else if (fallback != null) {
            stacks.put(fallback, clamp(unitCount, STACK_LIMIT));
        }

        return stacks;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass() && ((CompressionChain) o).variants.equals(variants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variants);
    }

    @Override
    public String toString() {
        var it = variants.iterator();

        if (!it.hasNext()) {
            return "[]";
        }

        var sb = new StringBuilder();
        sb.append('[');

        for (; ; ) {
            var stack = it.next();
            sb.append(stack.getCount());
            sb.append("x → ");
            sb.append(CompressionService.variantString(stack));

            if (!it.hasNext()) {
                return sb.append(']').toString();
            }

            sb.append(", ");
        }
    }
}

package gripe._90.megacells.misc;

import java.math.BigInteger;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.item.Item;

import appeng.api.stacks.AEItemKey;

public class CompressionChain extends ObjectArrayList<CompressionChain.Variant> {
    void add(AEItemKey item, int factor) {
        add(new Variant(item, factor));
    }

    public boolean containsVariant(AEItemKey item) {
        return stream().anyMatch(v -> v.item().equals(item));
    }

    public BigInteger unitFactor(AEItemKey item) {
        return stream()
                .filter(v -> v.item().equals(item))
                .findFirst()
                .map(i -> IntStream.rangeClosed(0, indexOf(i))
                        .mapToObj(this::get)
                        .map(v -> BigInteger.valueOf(v.factor()))
                        .reduce(BigInteger.ONE, BigInteger::multiply))
                .orElse(BigInteger.ONE);
    }

    public CompressionChain lastMultiplierSwapped() {
        var multipliers = stream().map(Variant::factor).collect(Collectors.toList());
        Collections.rotate(multipliers, -1);

        var items = stream().map(Variant::item).toList();
        var chain = new CompressionChain();

        for (var i = 0; i < items.size(); i++) {
            chain.add(items.get(i), multipliers.get(i));
        }

        return chain;
    }

    public CompressionChain limited(int limit) {
        var chain = new CompressionChain();
        chain.addAll(subList(0, limit));
        return chain;
    }

    public record Variant(AEItemKey item, int factor) {
        Variant(Item item, int factor) {
            this(AEItemKey.of(item), factor);
        }
    }
}

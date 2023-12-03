package gripe._90.megacells.misc;

import java.math.BigInteger;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import appeng.api.stacks.AEItemKey;

public class CompressionChain extends ObjectArrayList<CompressionService.Variant> {
    public void add(AEItemKey item, byte factor) {
        add(new CompressionService.Variant(item, factor));
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
        var multipliers = stream().map(CompressionService.Variant::factor).collect(Collectors.toList());
        Collections.rotate(multipliers, -1);

        var items = stream().map(CompressionService.Variant::item).toList();
        var chain = new CompressionChain();

        for (var i = 0; i < items.size(); i++) {
            chain.add(items.get(i), multipliers.get(i));
        }

        return chain;
    }
}

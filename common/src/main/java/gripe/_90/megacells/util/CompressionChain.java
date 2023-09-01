package gripe._90.megacells.util;

import java.math.BigInteger;
import java.util.Collections;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import appeng.api.stacks.AEItemKey;

public class CompressionChain extends ObjectArrayList<CompressionVariant> {
    public void add(AEItemKey item, int factor) {
        this.add(new CompressionVariant(item, factor));
    }

    public boolean containsVariant(AEItemKey item) {
        return this.stream().anyMatch(v -> v.item().equals(item));
    }

    public BigInteger unitFactor(AEItemKey item) {
        var variant = this.stream().filter(v -> v.item().equals(item)).findFirst();

        if (variant.isEmpty()) {
            return BigInteger.ONE;
        }

        var subChain = this.subList(0, indexOf(variant.get()) + 1);
        var factor = subChain.stream().map(CompressionVariant::longFactor).reduce(1L, Math::multiplyExact);
        return BigInteger.valueOf(factor);
    }

    public CompressionVariant last() {
        return get(size - 1);
    }

    public CompressionChain lastMultiplierSwapped() {
        var multipliers = this.stream().map(CompressionVariant::factor).collect(Collectors.toList());
        Collections.rotate(multipliers, -1);

        var items = this.stream().map(CompressionVariant::item).toList();
        var chain = new CompressionChain();

        for (var i = 0; i < items.size(); i++) {
            chain.add(items.get(i), multipliers.get(i));
        }

        return chain;
    }

    public CompressionChain decompressFrom(AEItemKey item) {
        var decompressionChain = new CompressionChain();
        var variant = this.stream().filter(v -> v.item().equals(item)).findFirst();

        if (variant.isPresent()) {
            for (var i = indexOf(variant.get()); i >= 0; i--) {
                decompressionChain.add(this.get(i));
            }
        }

        return decompressionChain;
    }
}

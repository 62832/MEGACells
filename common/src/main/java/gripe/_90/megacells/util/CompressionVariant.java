package gripe._90.megacells.util;

import net.minecraft.world.item.Item;

import appeng.api.stacks.AEItemKey;

public record CompressionVariant(AEItemKey item, int factor) {
    public CompressionVariant(Item item, int factor) {
        this(AEItemKey.of(item), factor);
    }

    public long longFactor() {
        return factor;
    }
}

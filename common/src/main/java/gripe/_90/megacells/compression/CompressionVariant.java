package gripe._90.megacells.compression;

import net.minecraft.world.item.Item;

import appeng.api.stacks.AEItemKey;

public record CompressionVariant(AEItemKey item, byte factor) {
    public CompressionVariant(Item item, byte factor) {
        this(AEItemKey.of(item), factor);
    }
}

package gripe._90.megacells.integration;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.items.storage.StorageTier;

public interface IntegrationHelper {
    default Function<Item.Properties, Item> createCell(StorageTier tier) {
        throw new UnsupportedOperationException();
    }

    default Function<Item.Properties, Item> createPortable(StorageTier tier) {
        throw new UnsupportedOperationException();
    }

    default void initUpgrades() {}
}

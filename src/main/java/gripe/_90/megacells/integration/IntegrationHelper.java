package gripe._90.megacells.integration;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.items.storage.StorageTier;

public interface IntegrationHelper {
    Function<Item.Properties, Item> createCell(StorageTier tier);

    Function<Item.Properties, Item> createPortable(StorageTier tier);

    void initUpgrades();
}

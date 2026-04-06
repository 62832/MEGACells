package gripe._90.megacells.integration.appsoul;

import java.util.function.Function;

import net.minecraft.world.item.Item;
import net.yxiao233.appliedsoul.common.item.SoulCellItem;

import appeng.items.storage.StorageTier;

import gripe._90.megacells.integration.IntegrationHelper;

public class AppSoulIntegration implements IntegrationHelper {
    @Override
    public Function<Item.Properties, Item> createCell(StorageTier tier) {
        return properties -> new SoulCellItem(properties, tier);
    }
}

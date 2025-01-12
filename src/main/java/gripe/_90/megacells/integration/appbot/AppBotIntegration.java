package gripe._90.megacells.integration.appbot;

import java.util.List;
import java.util.function.Function;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.storage.StorageTier;

import appbot.AppliedBotanics;
import appbot.item.ManaCellItem;
import appbot.item.PortableManaCellItem;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public class AppBotIntegration implements IntegrationHelper {
    @Override
    public Function<Item.Properties, Item> createCell(StorageTier tier) {
        return p -> new ManaCellItem(p, tier.componentSupplier().get(), tier.bytes() / 1024, tier.idleDrain());
    }

    @Override
    public Function<Item.Properties, Item> createPortable(StorageTier tier) {
        return p -> new PortableManaCellItem(p, tier.bytes() / 1024, tier.idleDrain());
    }

    @Override
    public void initUpgrades() {
        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.keyType().equals("mana")) {
                Upgrades.add(AEItems.VOID_CARD, cell.item(), 1, GuiText.StorageCells.getTranslationKey());

                if (cell.portable()) {
                    Upgrades.add(
                            MEGAItems.GREATER_ENERGY_CARD, cell.item(), 2, GuiText.PortableCells.getTranslationKey());
                }
            }
        }

        for (var portable : getCells()) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable, 2, GuiText.PortableCells.getTranslationKey());
        }
    }

    public static List<ItemLike> getCells() {
        return List.of(
                portable(StorageTier.SIZE_1K),
                portable(StorageTier.SIZE_4K),
                portable(StorageTier.SIZE_16K),
                portable(StorageTier.SIZE_64K),
                portable(StorageTier.SIZE_256K));
    }

    private static ItemLike portable(StorageTier tier) {
        return BuiltInRegistries.ITEM.get(AppliedBotanics.id("portable_mana_storage_cell_" + tier.namePrefix()));
    }
}

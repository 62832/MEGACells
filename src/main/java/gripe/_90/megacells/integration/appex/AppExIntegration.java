package gripe._90.megacells.integration.appex;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.storage.StorageTier;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.item.ExperiencePortableCellItem;
import es.degrassi.appexp.item.ExperienceStorageCell;

public class AppExIntegration implements IntegrationHelper {
    @Override
    public Function<Item.Properties, Item> createCell(StorageTier tier) {
        return p -> new ExperienceStorageCell(p, tier);
    }

    @Override
    public Function<Item.Properties, Item> createPortable(StorageTier tier) {
        return p -> new ExperiencePortableCellItem(tier, p, 0xcbd936);
    }

    @Override
    public void initUpgrades() {
        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.keyType().equals("experience")) {
                Upgrades.add(AEItems.VOID_CARD, cell.item(), 1, GuiText.StorageCells.getTranslationKey());

                if (cell.portable()) {
                    Upgrades.add(
                            MEGAItems.GREATER_ENERGY_CARD, cell.item(), 2, GuiText.PortableCells.getTranslationKey());
                }
            }
        }

        for (var cell : AExpItems.getPortables()) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
        }
    }
}

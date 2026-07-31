package gripe._90.megacells.integration.arseng;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.storage.StorageTier;

import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public class ArsEngIntegration implements IntegrationHelper {
    @Override
    public Function<Item.Properties, Item> createCell(StorageTier tier) {
        return p -> new SourceCellItem(p, tier);
    }

    @Override
    public Function<Item.Properties, Item> createPortable(StorageTier tier) {
        // TODO: pre-port this overrode getRecipeId() to point at MEGACells' own recipe path, but
        // that method returns ResourceLocation on this 1.21.1 jar and that type no longer exists in
        // 26.1 (renamed to Identifier), so it can't be overridden while ArsEng is still a compile-only
        // stub (see settings.gradle.kts). Restore the override once ArsEng ships a real 26.1 build.
        return p -> new PortableSourceCellItem(p, tier);
    }

    @Override
    public void initUpgrades() {
        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.keyType().equals("source")) {
                Upgrades.add(AEItems.VOID_CARD, cell.item(), 1, GuiText.StorageCells.getTranslationKey());

                if (cell.portable()) {
                    Upgrades.add(
                            MEGAItems.GREATER_ENERGY_CARD, cell.item(), 2, GuiText.PortableCells.getTranslationKey());
                }
            }
        }

        for (var cell : ArsEngItems.getPortables()) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
        }
    }
}

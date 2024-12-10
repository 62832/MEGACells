package gripe._90.megacells.integration.arseng;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.storage.StorageTier;

import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public class ArsEngIntegration implements IntegrationHelper {
    @Override
    public Function<Item.Properties, Item> createCell(StorageTier tier) {
        return p -> new SourceCellItem(p, tier);
    }

    @Override
    public Function<Item.Properties, Item> createPortable(StorageTier tier) {
        return p -> new PortableSourceCellItem(p, tier) {
            @Override
            public ResourceLocation getRecipeId() {
                return MEGACells.makeId("cells/portable/"
                        + Objects.requireNonNull(getRegistryName()).getPath());
            }
        };
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

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

public class ArsEngIntegration {
    public static Function<Item.Properties, Item> createSourceCell(StorageTier tier) {
        return p -> new SourceCellItem(p, tier, MEGAItems.MEGA_SOURCE_CELL_HOUSING);
    }

    public static Function<Item.Properties, Item> createSourcePortable(StorageTier tier) {
        return p -> new PortableSourceCellItem(p, tier) {
            @Override
            public ResourceLocation getRecipeId() {
                return MEGACells.makeId("cells/portable/"
                        + Objects.requireNonNull(getRegistryName()).getPath());
            }
        };
    }

    @SuppressWarnings("CodeBlock2Expr")
    public static void initUpgrades() {
        MEGAItems.getSourceCells().forEach(cell -> {
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey());
        });
        MEGAItems.getSourcePortables().forEach(cell -> {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
        });
        ArsEngItems.getPortables().forEach(cell -> {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
        });
    }
}

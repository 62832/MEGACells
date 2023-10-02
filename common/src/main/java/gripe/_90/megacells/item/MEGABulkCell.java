package gripe._90.megacells.item;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.item.cell.BulkCellInventory;

public class MEGABulkCell extends AEBaseItem implements ICellWorkbenchItem {
    public static final Handler HANDLER = new Handler();

    public MEGABulkCell(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(AEItemKey.filter(), is, 1);
    }

    @Override
    public void appendHoverText(ItemStack is, Level level, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        var inv = HANDLER.getCellInventory(is, null);

        if (inv != null) {
            var storedItem = inv.getStoredItem();
            var filterItem = inv.getFilterItem();

            if (storedItem != null) {
                lines.add(Tooltips.of(MEGATranslations.Contains.text(storedItem.getDisplayName())));
                var quantity = inv.getStoredQuantity();
                lines.add(Tooltips.of(MEGATranslations.Quantity.text(
                        quantity < Long.MAX_VALUE
                                ? Tooltips.ofNumber(quantity)
                                : MEGATranslations.ALot.text().withStyle(Tooltips.NUMBER_TEXT))));
            } else {
                lines.add(Tooltips.of(MEGATranslations.Empty.text()));
            }

            if (filterItem != null) {
                if (storedItem == null) {
                    lines.add(Tooltips.of(MEGATranslations.PartitionedFor.text(filterItem.getDisplayName())));
                } else {
                    if (!storedItem.equals(filterItem)) {
                        lines.add(MEGATranslations.MismatchedFilter.text().withStyle(ChatFormatting.DARK_RED));
                    }
                }
            } else {
                if (storedItem != null) {
                    lines.add(MEGATranslations.MismatchedFilter.text().withStyle(ChatFormatting.DARK_RED));
                } else {
                    lines.add(MEGATranslations.NotPartitioned.text());
                }
            }

            lines.add(Tooltips.of(MEGATranslations.Compression.text(
                    inv.isCompressionEnabled()
                            ? MEGATranslations.Enabled.text().withStyle(ChatFormatting.GREEN)
                            : MEGATranslations.Disabled.text().withStyle(ChatFormatting.RED))));
        }
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {}

    public static class Handler implements ICellHandler {
        private Handler() {}

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.getItem() instanceof MEGABulkCell;
        }

        @Nullable
        @Override
        public BulkCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
            Objects.requireNonNull(is, "Cannot create cell inventory for null itemstack");
            return isCell(is) ? new BulkCellInventory((MEGABulkCell) is.getItem(), is, container) : null;
        }
    }
}

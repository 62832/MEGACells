package gripe._90.megacells.item.cell;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.item.MEGABulkCell;

public class BulkCellHandler implements ICellHandler {

    public static final BulkCellHandler INSTANCE = new BulkCellHandler();

    private BulkCellHandler() {
    }

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof MEGABulkCell;
    }

    @Nullable
    @Override
    public BulkCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
        Objects.requireNonNull(is, "Cannot create cell inventory for null itemstack");

        var item = is.getItem();
        if (!(item instanceof MEGABulkCell cell)) {
            return null;
        }

        return new BulkCellInventory(cell, is, container);
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        var storedItem = handler.getStoredItem();
        var filterItem = handler.getFilterItem();

        if (storedItem != null) {
            lines.add(Tooltips.of(MEGATranslations.Contains.text(storedItem.getDisplayName())));
            var quantity = handler.getAvailableStacks().get(storedItem);
            lines.add(Tooltips.of(MEGATranslations.Quantity.text(Tooltips.ofNumber(quantity))));
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
            lines.add(Tooltips.of(MEGATranslations.NotPartitioned.text()));
        }

        lines.add(Tooltips.of(MEGATranslations.Compression.text(handler.compressionEnabled
                ? MEGATranslations.Enabled.text().withStyle(ChatFormatting.GREEN)
                : MEGATranslations.Disabled.text().withStyle(ChatFormatting.RED))));
    }
}

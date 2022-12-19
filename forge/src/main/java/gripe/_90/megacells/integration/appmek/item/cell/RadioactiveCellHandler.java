package gripe._90.megacells.integration.appmek.item.cell;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.integration.appmek.item.MEGARadioactiveCell;

public class RadioactiveCellHandler implements ICellHandler {
    public static final RadioactiveCellHandler INSTANCE = new RadioactiveCellHandler();

    private RadioactiveCellHandler() {
    }

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof MEGARadioactiveCell;
    }

    @Nullable
    @Override
    public RadioactiveCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
        return RadioactiveCellInventory.createInventory(is, container);
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        var containedType = handler.getAvailableStacks().getFirstKey();
        var filterItem = handler.getFilterItem();

        lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), RadioactiveCellInventory.MAX_BYTES));
        lines.add(Tooltips.of(containedType != null ? MEGATranslations.Contains.text(containedType.getDisplayName())
                : MEGATranslations.Empty.text()));

        if (filterItem != null) {
            if (containedType == null) {
                lines.add(Tooltips.of(MEGATranslations.PartitionedFor.text(filterItem.getDisplayName())));
            } else {
                if (!containedType.equals(filterItem)) {
                    lines.add(MEGATranslations.MismatchedFilter.text().withStyle(ChatFormatting.DARK_RED));
                }
            }
            if (handler.isBlackListed(filterItem)) {
                lines.add(MEGATranslations.FilterChemicalUnsupported.text().withStyle(ChatFormatting.DARK_RED));
            }
        } else {
            lines.add(Tooltips.of(MEGATranslations.NotPartitioned.text()));
        }
    }
}

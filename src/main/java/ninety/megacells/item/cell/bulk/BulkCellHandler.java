package ninety.megacells.item.cell.bulk;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;

public class BulkCellHandler implements ICellHandler {
    public static final BulkCellHandler INSTANCE = new BulkCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof IBulkCellItem;
    }

    @Nullable
    @Override
    public BulkCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
        return BulkCellInventory.createInventory(is, container);
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        var containedType = handler.getAvailableStacks().getFirstKey();
        var filterSlots = handler.getConfigInventory().keySet().stream().toList();

        lines.add(containedType == null ? Tooltips.of("Empty")
                : Tooltips.of(Tooltips.of("Contains: "),
                        Tooltips.of(containedType.wrapForDisplayOrFilter().getHoverName())));

        if (!filterSlots.isEmpty()) {
            if (filterSlots.size() == 1) {
                lines.add(Tooltips.of(Tooltips.of("Partitioned for: "), filterSlots.get(0).getDisplayName()));
            } else {
                lines.add(Tooltips.of("Partitioned for:"));
                for (var slot : filterSlots) {
                    lines.add(slot.getDisplayName());
                }
            }
        } else {
            lines.add(Tooltips.of("Not Partitioned"));
        }
    }
}

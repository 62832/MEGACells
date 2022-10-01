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

        if (containedType != null) {
            lines.add(Tooltips.of(Tooltips.of("Contains: "), containedType.getDisplayName()));
            var quantity = handler.getAvailableStacks().get(containedType);
            lines.add(Tooltips.of(Tooltips.of("Quantity: "), Tooltips.ofNumber(quantity)));
        } else {
            lines.add(Tooltips.of("Empty"));
        }

        if (!filterSlots.isEmpty()) {
            if (containedType == null) {
                lines.add(Tooltips.of(Tooltips.of("Partitioned for: "), filterSlots.get(0).getDisplayName()));
            }
        } else {
            lines.add(Tooltips.of("Not Partitioned"));
        }
    }
}

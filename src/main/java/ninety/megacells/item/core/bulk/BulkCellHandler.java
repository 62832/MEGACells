package ninety.megacells.item.core.bulk;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import ninety.megacells.item.MEGABulkCell;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BulkCellHandler implements ICellHandler {

    public static final BulkCellHandler INSTANCE = new BulkCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return BulkCellInventory.isCell(is);
    }

    @Nullable
    @Override
    public BulkCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
        return BulkCellInventory.createInventory(is, host);
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        var containedType = handler.getAvailableStacks().getFirstKey();
        lines.add(containedType == null ? Tooltips.of("Empty")
                : Tooltips.of(Tooltips.of("Contains: "),
                Tooltips.of(containedType.wrapForDisplayOrFilter().getHoverName())));
    }
}

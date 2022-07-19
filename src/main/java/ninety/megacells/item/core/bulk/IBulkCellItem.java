package ninety.megacells.item.core.bulk;

import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.util.ConfigInventory;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IBulkCellItem extends ICellWorkbenchItem {
    AEKeyType getKeyType();

    default boolean storesInStorageCell() {
        return false;
    }

    default boolean isStorageCell(ItemStack is) {
        return true;
    }

    ConfigInventory getConfigInventory(ItemStack is);

    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        BulkCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }
}

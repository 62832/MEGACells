package gripe._90.megacells.misc;

import net.minecraft.world.item.ItemStack;

import appeng.api.storage.cells.ICellWorkbenchItem;

public interface CellWorkbenchHost {
    ICellWorkbenchItem getCell();

    ItemStack mega$getContainedStack();

    void saveChanges();
}

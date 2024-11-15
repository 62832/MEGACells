package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.RestrictedInputSlot;

import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.item.part.CellDockPart;

/**
 * @see appeng.menu.implementations.MEChestMenu
 */
public class CellDockMenu extends AEBaseMenu {
    public CellDockMenu(int id, Inventory playerInventory, CellDockPart dock) {
        super(MEGAMenus.CELL_DOCK, id, playerInventory, dock);
        addSlot(
                new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.STORAGE_CELLS, dock.getCellInventory(), 0),
                SlotSemantics.STORAGE_CELL);
        createPlayerInventorySlots(playerInventory);
    }
}

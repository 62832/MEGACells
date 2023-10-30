package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;

import gripe._90.megacells.item.part.CellDockPart;

/**
 * @see appeng.menu.implementations.ChestMenu
 */
public class CellDockMenu extends AEBaseMenu {
    public static final MenuType<CellDockMenu> TYPE =
            MenuTypeBuilder.create(CellDockMenu::new, CellDockPart.class).build("cell_dock");

    public CellDockMenu(int id, Inventory playerInventory, CellDockPart dock) {
        super(TYPE, id, playerInventory, dock);
        addSlot(
                new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.STORAGE_CELLS, dock.getCellInventory(), 0),
                SlotSemantics.STORAGE_CELL);
        createPlayerInventorySlots(playerInventory);
    }
}

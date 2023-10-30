package gripe._90.megacells.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;

import gripe._90.megacells.menu.CellDockMenu;

public class CellDockScreen extends AEBaseScreen<CellDockMenu> {
    public CellDockScreen(CellDockMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        widgets.addOpenPriorityButton();
    }
}

package gripe._90.megacells.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;

import gripe._90.megacells.menu.CellDockMenu;

/**
 * @see appeng.client.gui.implementations.MEChestScreen
 */
public class CellDockScreen extends AEBaseScreen<CellDockMenu> {
    public CellDockScreen(CellDockMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        widgets.addOpenPriorityButton();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        if (!title.getString().isEmpty()) {
            setTextContent(TEXT_ID_DIALOG_TITLE, title);
        }
    }
}

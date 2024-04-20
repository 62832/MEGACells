package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;

import appeng.helpers.InterfaceLogicHost;
import appeng.menu.implementations.InterfaceMenu;

import gripe._90.megacells.definition.MEGAMenus;

public class MEGAInterfaceMenu extends InterfaceMenu {
    public MEGAInterfaceMenu(int id, Inventory ip, InterfaceLogicHost host) {
        super(MEGAMenus.MEGA_INTERFACE, id, ip, host);
    }
}

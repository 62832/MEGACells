package gripe._90.megacells.integration.appliede;

import net.minecraft.world.entity.player.Inventory;

import gripe._90.appliede.me.misc.EMCInterfaceLogicHost;
import gripe._90.appliede.menu.EMCInterfaceMenu;

public class MEGAEMCInterfaceMenu extends EMCInterfaceMenu {
    public MEGAEMCInterfaceMenu(int id, Inventory playerInventory, EMCInterfaceLogicHost host) {
        super(AppliedEIntegration.EMC_INTERFACE_MENU, id, playerInventory, host);
    }
}

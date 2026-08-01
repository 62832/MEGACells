package gripe._90.megacells.integration.appliede;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.parts.IPartItem;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;

import gripe._90.appliede.me.misc.EMCInterfaceLogic;
import gripe._90.appliede.part.EMCInterfacePart;
import gripe._90.megacells.definition.MEGAItems;

public class MEGAEMCInterfacePart extends EMCInterfacePart {
    public MEGAEMCInterfacePart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected EMCInterfaceLogic createLogic() {
        return new EMCInterfaceLogic(getMainNode(), this, getPartItem().asItem(), 18);
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(AppliedEIntegration.MEGA_EMC_INTERFACE_MENU.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.open(AppliedEIntegration.MEGA_EMC_INTERFACE_MENU.get(), player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.MEGA_EMC_INTERFACE.stack();
    }
}

package gripe._90.megacells.integration.appliede;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

import gripe._90.appliede.block.EMCInterfaceBlockEntity;
import gripe._90.appliede.me.misc.EMCInterfaceLogic;

public class MEGAEMCInterfaceBlockEntity extends EMCInterfaceBlockEntity {
    public MEGAEMCInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected EMCInterfaceLogic createLogic() {
        return new EMCInterfaceLogic(getMainNode(), this, getItemFromBlockEntity(), 18);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(AppliedEIntegration.EMC_INTERFACE_MENU, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(AppliedEIntegration.EMC_INTERFACE_MENU, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AppliedEIntegration.EMC_INTERFACE.stack();
    }
}

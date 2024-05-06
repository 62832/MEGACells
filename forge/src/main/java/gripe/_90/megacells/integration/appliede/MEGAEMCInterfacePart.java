package gripe._90.megacells.integration.appliede;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.parts.PartModel;

import gripe._90.appliede.me.misc.EMCInterfaceLogic;
import gripe._90.appliede.part.EMCInterfacePart;
import gripe._90.megacells.MEGACells;

public class MEGAEMCInterfacePart extends EMCInterfacePart {
    private static final ResourceLocation MODEL_BASE = MEGACells.makeId("part/mega_emc_interface");

    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/interface_has_channel"));

    public MEGAEMCInterfacePart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected EMCInterfaceLogic createLogic() {
        return new EMCInterfaceLogic(getMainNode(), this, getPartItem().asItem(), 18);
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
    public IPartModel getStaticModels() {
        if (isActive() && isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AppliedEIntegration.CABLE_EMC_INTERFACE.stack();
    }
}

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
import appeng.menu.locator.MenuHostLocator;
import appeng.parts.PartModel;

import gripe._90.appliede.me.misc.EMCInterfaceLogic;
import gripe._90.appliede.part.EMCInterfacePart;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;

public class MEGAEMCInterfacePart extends EMCInterfacePart {
    private static final ResourceLocation MODEL_BASE = MEGACells.makeId("part/mega_emc_interface");

    @PartModels
    private static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_off"));

    @PartModels
    private static final PartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_on"));

    @PartModels
    private static final PartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/interface_has_channel"));

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
    public IPartModel getStaticModels() {
        return isPowered() ? isActive() ? MODELS_HAS_CHANNEL : MODELS_ON : MODELS_OFF;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.MEGA_EMC_INTERFACE.stack();
    }
}

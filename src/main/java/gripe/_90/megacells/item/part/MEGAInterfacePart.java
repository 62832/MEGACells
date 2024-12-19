package gripe._90.megacells.item.part;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.helpers.InterfaceLogic;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.parts.PartModel;
import appeng.parts.misc.InterfacePart;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;

public class MEGAInterfacePart extends InterfacePart {
    private static final ResourceLocation MODEL_BASE = MEGACells.makeId("part/mega_interface");

    @PartModels
    private static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_off"));

    @PartModels
    private static final PartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_on"));

    @PartModels
    private static final PartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/interface_has_channel"));

    public MEGAInterfacePart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected InterfaceLogic createLogic() {
        return new InterfaceLogic(getMainNode(), this, getPartItem().asItem(), 18);
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(MEGAMenus.MEGA_INTERFACE.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.open(MEGAMenus.MEGA_INTERFACE.get(), player, subMenu.getLocator());
    }

    @Override
    public IPartModel getStaticModels() {
        return isPowered() ? isActive() ? MODELS_HAS_CHANNEL : MODELS_ON : MODELS_OFF;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.MEGA_INTERFACE.stack();
    }
}

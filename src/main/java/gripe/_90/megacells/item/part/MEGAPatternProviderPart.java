package gripe._90.megacells.item.part;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.parts.PartModel;
import appeng.parts.crafting.PatternProviderPart;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;

public class MEGAPatternProviderPart extends PatternProviderPart {
    private static final ResourceLocation MODEL_BASE = MEGACells.makeId("part/mega_pattern_provider");

    @PartModels
    private static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_off"));

    @PartModels
    private static final PartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_on"));

    @PartModels
    private static final PartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/interface_has_channel"));

    public MEGAPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public PatternProviderLogic createLogic() {
        return MEGAPatternProviderBlock.createLogic(getMainNode(), this);
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(MEGAMenus.MEGA_PATTERN_PROVIDER.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(MEGAMenus.MEGA_PATTERN_PROVIDER.get(), player, subMenu.getLocator());
    }

    @Override
    public IPartModel getStaticModels() {
        return isPowered() ? isActive() ? MODELS_HAS_CHANNEL : MODELS_ON : MODELS_OFF;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.MEGA_PATTERN_PROVIDER.stack();
    }
}

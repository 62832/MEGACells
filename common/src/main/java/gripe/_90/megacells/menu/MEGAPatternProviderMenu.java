package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.PatternProviderMenu;

import gripe._90.megacells.definition.MEGAMenus;

public class MEGAPatternProviderMenu extends PatternProviderMenu {
    public MEGAPatternProviderMenu(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(MEGAMenus.MEGA_PATTERN_PROVIDER, id, playerInventory, host);
    }
}

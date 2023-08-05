package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;

public class MEGAPatternProviderMenu extends PatternProviderMenu {
    public static final MenuType<MEGAPatternProviderMenu> TYPE = MenuTypeBuilder.create(
                    MEGAPatternProviderMenu::new, PatternProviderLogicHost.class)
            .build("mega_pattern_provider");

    protected MEGAPatternProviderMenu(
            MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }
}

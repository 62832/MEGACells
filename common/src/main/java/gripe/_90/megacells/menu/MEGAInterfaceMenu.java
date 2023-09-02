package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.helpers.InterfaceLogicHost;
import appeng.menu.implementations.InterfaceMenu;
import appeng.menu.implementations.MenuTypeBuilder;

public class MEGAInterfaceMenu extends InterfaceMenu {
    public static final MenuType<MEGAInterfaceMenu> TYPE = MenuTypeBuilder.create(
                    MEGAInterfaceMenu::new, InterfaceLogicHost.class)
            .build("mega_interface");

    public MEGAInterfaceMenu(MenuType<MEGAInterfaceMenu> menuType, int id, Inventory ip, InterfaceLogicHost host) {
        super(menuType, id, ip, host);
    }
}

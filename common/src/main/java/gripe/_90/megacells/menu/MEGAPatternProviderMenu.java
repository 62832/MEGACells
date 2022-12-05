package gripe._90.megacells.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.config.SecurityPermissions;
import appeng.core.definitions.AEItems;
import appeng.helpers.iface.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.AEItemDefinitionFilter;

public class MEGAPatternProviderMenu extends PatternProviderMenu {

    public static final MenuType<MEGAPatternProviderMenu> TYPE = MenuTypeBuilder
            .create(MEGAPatternProviderMenu::new, PatternProviderLogicHost.class)
            .requirePermission(SecurityPermissions.BUILD)
            .build("mega_pattern_provider");

    public MEGAPatternProviderMenu(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(TYPE, id, playerInventory, host);
        ((AppEngInternalInventory) logic.getPatternInv())
                .setFilter(new AEItemDefinitionFilter(AEItems.PROCESSING_PATTERN));
    }
}

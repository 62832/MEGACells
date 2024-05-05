package gripe._90.megacells.definition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import appeng.core.AppEng;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

import gripe._90.megacells.item.part.CellDockPart;
import gripe._90.megacells.menu.CellDockMenu;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

public class MEGAMenus {
    private static final Map<ResourceLocation, MenuType<?>> MENU_TYPES = new HashMap<>();

    public static Map<ResourceLocation, MenuType<?>> getMenuTypes() {
        return Collections.unmodifiableMap(MENU_TYPES);
    }

    public static final MenuType<MEGAInterfaceMenu> MEGA_INTERFACE =
            create("mega_interface", MEGAInterfaceMenu::new, InterfaceLogicHost.class);
    public static final MenuType<MEGAPatternProviderMenu> MEGA_PATTERN_PROVIDER =
            create("mega_pattern_provider", MEGAPatternProviderMenu::new, PatternProviderLogicHost.class);
    public static final MenuType<CellDockMenu> CELL_DOCK = create("cell_dock", CellDockMenu::new, CellDockPart.class);

    public static <C extends AEBaseMenu, I> MenuType<C> create(
            String id, MenuTypeBuilder.MenuFactory<C, I> factory, Class<I> host) {
        var menu = MenuTypeBuilder.create(factory, host).build(id);
        MENU_TYPES.put(AppEng.makeId(id), menu);
        return menu;
    }
}

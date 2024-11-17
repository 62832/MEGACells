package gripe._90.megacells.definition;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.InterfaceMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.cell.PortableCellWorkbenchMenuHost;
import gripe._90.megacells.item.part.CellDockPart;
import gripe._90.megacells.menu.CellDockMenu;
import gripe._90.megacells.menu.PortableCellWorkbenchMenu;
import gripe._90.megacells.mixin.PatternProviderMenuAccessor;

public final class MEGAMenus {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, MEGACells.MODID);

    public static final Supplier<MenuType<InterfaceMenu>> MEGA_INTERFACE =
            createTyped("mega_interface", InterfaceMenu::new, InterfaceLogicHost.class);
    public static final Supplier<MenuType<PatternProviderMenu>> MEGA_PATTERN_PROVIDER =
            createTyped("mega_pattern_provider", PatternProviderMenuAccessor::create, PatternProviderLogicHost.class);

    public static final Supplier<MenuType<CellDockMenu>> CELL_DOCK =
            create("cell_dock", CellDockMenu::new, CellDockPart.class);
    public static final Supplier<MenuType<PortableCellWorkbenchMenu>> PORTABLE_CELL_WORKBENCH =
            create("portable_cell_workbench", PortableCellWorkbenchMenu::new, PortableCellWorkbenchMenuHost.class);

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return DR.register(id, () -> MenuTypeBuilder.create(factory, host).build(MEGACells.makeId(id)));
    }

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> createTyped(
            String id, MenuTypeBuilder.TypedMenuFactory<M, H> factory, Class<H> host) {
        return DR.register(id, () -> MenuTypeBuilder.create(factory, host).build(MEGACells.makeId(id)));
    }
}

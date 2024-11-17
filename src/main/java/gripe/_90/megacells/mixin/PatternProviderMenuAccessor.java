package gripe._90.megacells.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.PatternProviderMenu;

// TODO: Remove that pointless extra constructor from upstream AE2 so that this can also go
@Mixin(PatternProviderMenu.class)
public interface PatternProviderMenuAccessor {
    @Invoker(value = "<init>")
    static PatternProviderMenu create(
            MenuType<? extends PatternProviderMenu> menuType, int id, Inventory ip, PatternProviderLogicHost host) {
        throw new AssertionError();
    }
}

package gripe._90.megacells.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.upgrades.IUpgradeInventory;

import gripe._90.megacells.item.cell.PortableCellWorkbenchMenuHost;

/**
 * Used to "override" an otherwise {@code final} method for upgrade inventories.
 */
@Mixin(ItemMenuHost.class)
public abstract class ItemMenuHostMixin {
    @ModifyReturnValue(method = "getUpgrades", at = @At("RETURN"))
    private IUpgradeInventory getPortableCellWorkbenchUpgrades(IUpgradeInventory original) {
        return ((ItemMenuHost<?>) (Object) this) instanceof PortableCellWorkbenchMenuHost workbench
                ? workbench.getCellUpgrades()
                : original;
    }
}

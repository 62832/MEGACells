package gripe._90.megacells.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.upgrades.IUpgradeInventory;

import gripe._90.megacells.item.cell.PortableCellWorkbenchMenuHost;

/**
 * Used to "override" an otherwise {@code final} method for upgrade inventories.
 */
@Mixin(ItemMenuHost.class)
public abstract class ItemMenuHostMixin {
    @Inject(method = "getUpgrades", at = @At("HEAD"), cancellable = true)
    private void getPortableCellWorkbenchUpgrades(CallbackInfoReturnable<IUpgradeInventory> cir) {
        if (((ItemMenuHost<?>) (Object) this) instanceof PortableCellWorkbenchMenuHost workbench) {
            cir.setReturnValue(workbench.getCachedUpgrades());
        }
    }
}

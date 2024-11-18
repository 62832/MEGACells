package gripe._90.megacells.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.item.ItemStack;

import appeng.blockentity.misc.CellWorkbenchBlockEntity;
import appeng.util.inv.AppEngInternalInventory;

import gripe._90.megacells.misc.CellWorkbenchHost;

@Mixin(CellWorkbenchBlockEntity.class)
public abstract class CellWorkbenchBlockEntityMixin implements CellWorkbenchHost {
    @Shadow
    @Final
    private AppEngInternalInventory cell;

    @Override
    public ItemStack mega$getContainedStack() {
        return cell.getStackInSlot(0);
    }
}

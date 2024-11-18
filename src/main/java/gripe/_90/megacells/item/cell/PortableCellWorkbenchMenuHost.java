package gripe._90.megacells.item.cell;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.locator.ItemMenuHostLocator;

import gripe._90.megacells.misc.CellWorkbenchHost;

/**
 * See {@link appeng.blockentity.misc.CellWorkbenchBlockEntity}
 */
public class PortableCellWorkbenchMenuHost extends ItemMenuHost<PortableCellWorkbenchItem>
        implements ISegmentedInventory, IConfigurableObject, IConfigInvHost, CellWorkbenchHost {
    private final Supplier<PortableCellWorkbenchInventory> cellInv =
            new StackDependentSupplier<>(this::getItemStack, PortableCellWorkbenchInventory::new);

    public PortableCellWorkbenchMenuHost(PortableCellWorkbenchItem item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
    }

    @Override
    public ICellWorkbenchItem getCell() {
        return cellInv.get().getCell();
    }

    @Override
    public ItemStack mega$getContainedStack() {
        return cellInv.get().getStackInSlot(0);
    }

    @Override
    public IConfigManager getConfigManager() {
        return cellInv.get().getConfigManager();
    }

    @Override
    public GenericStackInv getConfig() {
        return cellInv.get().getConfig();
    }

    public IUpgradeInventory getCellUpgrades() {
        return cellInv.get().getCellUpgrades();
    }

    @Override
    public void saveChanges() {
        cellInv.get().saveChanges();
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        return id.equals(ISegmentedInventory.CELLS) ? cellInv.get() : null;
    }
}

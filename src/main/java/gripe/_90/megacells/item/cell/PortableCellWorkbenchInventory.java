package gripe._90.megacells.item.cell;

import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.ItemLike;

import appeng.api.config.CopyMode;
import appeng.api.config.Settings;
import appeng.api.ids.AEComponents;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.blockentity.misc.CellWorkbenchBlockEntity;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;

/**
 * See {@link appeng.blockentity.misc.CellWorkbenchBlockEntity}
 */
public class PortableCellWorkbenchInventory extends AppEngInternalInventory implements InternalInventoryHost {
    private final ItemStack stack;

    private final GenericStackInv config =
            new GenericStackInv(this::onConfigChanged, GenericStackInv.Mode.CONFIG_TYPES, 63);
    private final IConfigManager manager = IConfigManager.builder(this::saveChanges)
            .registerSetting(Settings.COPY_MODE, CopyMode.CLEAR_ON_REMOVE)
            .build();

    public PortableCellWorkbenchInventory(ItemStack stack) {
        super(null, 1, 1, new Filter());
        this.stack = stack;

        setHost(this);
        setEnableClientEvents(true);
        fromItemContainerContents(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        config.readFromList(stack.getOrDefault(AEComponents.EXPORTED_CONFIG_INV, List.of()));
    }

    ICellWorkbenchItem getCell() {
        if (getStackInSlot(0).isEmpty()) {
            return null;
        }

        return getStackInSlot(0).getItem() instanceof ICellWorkbenchItem cell ? cell : null;
    }

    GenericStackInv getConfig() {
        return config;
    }

    IConfigManager getConfigManager() {
        return manager;
    }

    private ConfigInventory getCellConfigInventory() {
        var cell = getCell();
        return cell != null ? cell.getConfigInventory(getStackInSlot(0)) : null;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var configInventory = getCellConfigInventory();

        if (configInventory != null) {
            if (!configInventory.isEmpty()) {
                CellWorkbenchBlockEntity.copy(configInventory, config);
            } else {
                CellWorkbenchBlockEntity.copy(config, configInventory);
                CellWorkbenchBlockEntity.copy(configInventory, config);
            }
        } else if (getConfigManager().getSetting(Settings.COPY_MODE) == CopyMode.CLEAR_ON_REMOVE) {
            config.clear();
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        saveChanges();
    }

    private void onConfigChanged() {
        var c = getCellConfigInventory();

        if (c != null) {
            CellWorkbenchBlockEntity.copy(config, c);
            CellWorkbenchBlockEntity.copy(c, config);
        }

        saveChanges();
    }

    void saveChanges() {
        stack.set(DataComponents.CONTAINER, toItemContainerContents());
        stack.set(AEComponents.EXPORTED_CONFIG_INV, config.toList());
        stack.set(AEComponents.EXPORTED_SETTINGS, manager.exportSettings());
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    IUpgradeInventory getCellUpgrades() {
        var cell = getCell();
        return cell != null
                ? new ProxiedUpgradeInventory(cell.getUpgrades(getStackInSlot(0)), this)
                : UpgradeInventories.empty();
    }

    private static class ProxiedUpgradeInventory extends AppEngInternalInventory implements IUpgradeInventory {
        private final IUpgradeInventory delegate;

        public ProxiedUpgradeInventory(IUpgradeInventory delegate, InternalInventoryHost host) {
            super(host, delegate.size(), 1);
            this.delegate = delegate;
        }

        @Override
        public ItemLike getUpgradableItem() {
            return delegate.getUpgradableItem();
        }

        @Override
        public int getInstalledUpgrades(ItemLike u) {
            return delegate.getInstalledUpgrades(u);
        }

        @Override
        public int getMaxInstalled(ItemLike u) {
            return delegate.getMaxInstalled(u);
        }

        @Override
        public void readFromNBT(CompoundTag data, String subtag, HolderLookup.Provider registries) {
            delegate.readFromNBT(data, subtag, registries);
        }

        @Override
        public void writeToNBT(CompoundTag data, String subtag, HolderLookup.Provider registries) {
            delegate.writeToNBT(data, subtag, registries);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            return delegate.getStackInSlot(slotIndex);
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            delegate.setItemDirect(slotIndex, stack);
            onContentsChanged(slotIndex);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            var extracted = delegate.extractItem(slot, amount, simulate);

            if (!simulate && !extracted.isEmpty()) {
                onContentsChanged(slot);
            }

            return extracted;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return delegate.isItemValid(slot, stack);
        }

        @Override
        protected boolean eventsEnabled() {
            return true;
        }
    }

    private static class Filter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return stack.getItem() instanceof ICellWorkbenchItem;
        }
    }
}

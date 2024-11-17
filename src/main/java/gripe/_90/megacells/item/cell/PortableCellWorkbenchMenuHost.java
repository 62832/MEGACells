package gripe._90.megacells.item.cell;

import java.util.Collections;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.ItemLike;

import appeng.api.config.CopyMode;
import appeng.api.config.Settings;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.misc.CellWorkbenchBlockEntity;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

/**
 * See {@link appeng.blockentity.misc.CellWorkbenchBlockEntity}
 */
public class PortableCellWorkbenchMenuHost extends ItemMenuHost<PortableCellWorkbenchItem>
        implements InternalInventoryHost, ISegmentedInventory, IConfigurableObject, IConfigInvHost {
    private final AppEngInternalInventory cellInv = new AppEngInternalInventory(this, 1);
    private final GenericStackInv config =
            new GenericStackInv(this::configChanged, GenericStackInv.Mode.CONFIG_TYPES, 63);

    public PortableCellWorkbenchMenuHost(PortableCellWorkbenchItem item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
        cellInv.setEnableClientEvents(true);
        cellInv.fromItemContainerContents(
                getItemStack().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        config.readFromList(getItemStack().getOrDefault(AEComponents.EXPORTED_CONFIG_INV, Collections.emptyList()));
    }

    public ItemStack getContainedStack() {
        return cellInv.getStackInSlot(0);
    }

    public ICellWorkbenchItem getCell() {
        if (getContainedStack().isEmpty()) {
            return null;
        }

        return getContainedStack().getItem() instanceof ICellWorkbenchItem cell ? cell : null;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        return id.equals(ISegmentedInventory.CELLS) ? cellInv : null;
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

    private void saveChanges() {
        getItemStack().set(DataComponents.CONTAINER, cellInv.toItemContainerContents());
        getItemStack().set(AEComponents.EXPORTED_CONFIG_INV, config.toList());
    }

    private void configChanged() {
        var c = getCellConfigInventory();

        if (c != null) {
            CellWorkbenchBlockEntity.copy(config, c);
            CellWorkbenchBlockEntity.copy(c, config);
        }

        saveChanges();
    }

    private ConfigInventory getCellConfigInventory() {
        var cell = getCell();

        if (cell == null) {
            return null;
        }

        var is = cellInv.getStackInSlot(0);

        if (is.isEmpty()) {
            return null;
        }

        return cell.getConfigInventory(is);
    }

    @Override
    public IConfigManager getConfigManager() {
        return IConfigManager.builder(this::getItemStack)
                .registerSetting(Settings.COPY_MODE, CopyMode.CLEAR_ON_REMOVE)
                .build();
    }

    @Override
    public GenericStackInv getConfig() {
        return config;
    }

    public IUpgradeInventory getCellUpgrades() {
        var cell = getCell();

        if (cell == null) {
            return UpgradeInventories.empty();
        }

        if (getContainedStack().isEmpty()) {
            return UpgradeInventories.empty();
        }

        var inv = cell.getUpgrades(getContainedStack());
        return inv == null ? UpgradeInventories.empty() : new ProxiedUpgradeInventory(inv, this);
    }

    private static class ProxiedUpgradeInventory extends AppEngInternalInventory implements IUpgradeInventory {
        private final IUpgradeInventory delegate;

        public ProxiedUpgradeInventory(IUpgradeInventory inventory, InternalInventoryHost host) {
            super(host, inventory.size(), 1);
            delegate = inventory;
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
}

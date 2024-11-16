package gripe._90.megacells.item.cell;

import java.util.Collections;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemContainerContents;

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
 * <p>
 * FIXME: Currently suffers from a dupe/void bug due to the config and upgrades on the cell within not saving properly.
 * This was more or less expected attempting to copy-paste the logic for a block entity to what is instead an item.
 */
public class PortableCellWorkbenchMenuHost extends ItemMenuHost<PortableCellWorkbenchItem>
        implements InternalInventoryHost, ISegmentedInventory, IConfigurableObject, IConfigInvHost {
    // FIXME: Ideally this should use a supplier-based inventory as with other ItemMenuHost impls
    private final AppEngInternalInventory cellInv = new AppEngInternalInventory(this, 1);
    private final GenericStackInv config =
            new GenericStackInv(this::configChanged, GenericStackInv.Mode.CONFIG_TYPES, 63);

    private IUpgradeInventory cachedUpgrades;
    private ConfigInventory cachedConfig;
    private boolean locked;

    public PortableCellWorkbenchMenuHost(PortableCellWorkbenchItem item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
        cellInv.setEnableClientEvents(true);
        cellInv.fromItemContainerContents(
                getItemStack().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        config.readFromList(getItemStack().getOrDefault(AEComponents.EXPORTED_CONFIG_INV, Collections.emptyList()));
    }

    public ICellWorkbenchItem getCell() {
        if (cellInv.getStackInSlot(0).isEmpty()) {
            return null;
        }

        return cellInv.getStackInSlot(0).getItem() instanceof ICellWorkbenchItem cell ? cell : null;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        return id.equals(ISegmentedInventory.CELLS) ? cellInv : null;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (inv == cellInv && !locked) {
            locked = true;

            try {
                cachedUpgrades = null;
                cachedConfig = null;

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
                    saveChanges();
                }
            } finally {
                locked = false;
            }
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
        if (locked) {
            return;
        }

        locked = true;

        try {
            var c = getCellConfigInventory();

            if (c != null) {
                CellWorkbenchBlockEntity.copy(config, c);
                CellWorkbenchBlockEntity.copy(c, config);
            }
        } finally {
            locked = false;
        }
    }

    private ConfigInventory getCellConfigInventory() {
        if (cachedConfig == null) {
            var cell = getCell();

            if (cell == null) {
                return null;
            }

            var is = cellInv.getStackInSlot(0);

            if (is.isEmpty()) {
                return null;
            }

            var inv = cell.getConfigInventory(is);

            if (inv == null) {
                return null;
            }

            cachedConfig = inv;
        }

        return cachedConfig;
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

    public IUpgradeInventory getCachedUpgrades() {
        if (cachedUpgrades == null) {
            var cell = getCell();

            if (cell == null) {
                return UpgradeInventories.empty();
            }

            var is = cellInv.getStackInSlot(0);

            if (is.isEmpty()) {
                return UpgradeInventories.empty();
            }

            var inv = cell.getUpgrades(is);

            if (inv == null) {
                return UpgradeInventories.empty();
            }

            cachedUpgrades = inv;
        }

        return cachedUpgrades;
    }
}

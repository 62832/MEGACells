package gripe._90.megacells.menu;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Iterators;

import it.unimi.dsi.fastutil.shorts.ShortSet;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.CopyMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.util.IConfigManager;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.CellWorkbenchMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.CellPartitionSlot;
import appeng.menu.slot.IPartitionSlotHost;
import appeng.menu.slot.OptionalRestrictedInputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.util.EnumCycler;
import appeng.util.inv.SupplierInternalInventory;

import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.item.cell.PortableCellWorkbenchMenuHost;

/**
 * See {@link appeng.menu.implementations.CellWorkbenchMenu}
 */
public class PortableCellWorkbenchMenu extends UpgradeableMenu<PortableCellWorkbenchMenuHost>
        implements IPartitionSlotHost, CompressionCutoffHost {
    @GuiSync(2)
    public CopyMode copyMode = CopyMode.CLEAR_ON_REMOVE;

    public PortableCellWorkbenchMenu(int id, Inventory ip, PortableCellWorkbenchMenuHost host) {
        super(MEGAMenus.PORTABLE_CELL_WORKBENCH.get(), id, ip, host);
        registerClientAction(CellWorkbenchMenu.ACTION_NEXT_COPYMODE, this::nextWorkBenchCopyMode);
        registerClientAction(CellWorkbenchMenu.ACTION_PARTITION, this::partition);
        registerClientAction(CellWorkbenchMenu.ACTION_CLEAR, this::clear);
        registerClientAction(CellWorkbenchMenu.ACTION_SET_FUZZY_MODE, FuzzyMode.class, this::setCellFuzzyMode);
        registerClientAction(CompressionCutoffHost.ACTION_SET_COMPRESSION_LIMIT, this::mega$nextCompressionLimit);
    }

    public void setCellFuzzyMode(FuzzyMode fuzzyMode) {
        if (isClientSide()) {
            sendClientAction(CellWorkbenchMenu.ACTION_SET_FUZZY_MODE, fuzzyMode);
        } else {
            var cell = getHost().getCell();

            if (cell != null) {
                cell.setFuzzyMode(getWorkbenchItem(), fuzzyMode);
                getHost().saveChanges();
            }
        }
    }

    public void nextWorkBenchCopyMode() {
        if (isClientSide()) {
            sendClientAction(CellWorkbenchMenu.ACTION_NEXT_COPYMODE);
        } else {
            getHost().getConfigManager().putSetting(Settings.COPY_MODE, EnumCycler.next(getWorkBenchCopyMode()));
        }
    }

    @Override
    public void mega$nextCompressionLimit() {
        if (isClientSide()) {
            sendClientAction(CompressionCutoffHost.ACTION_SET_COMPRESSION_LIMIT);
        } else {
            if (BulkCellItem.HANDLER.getCellInventory(getHost().mega$getContainedStack(), null)
                    instanceof BulkCellInventory bulkCell) {
                var currentLimit = bulkCell.getCompressionCutoff();
                bulkCell.setCompressionCutoff(
                        currentLimit == 1 ? bulkCell.getCompressionChain().size() : currentLimit - 1);
                getHost().saveChanges();
            }
        }
    }

    private CopyMode getWorkBenchCopyMode() {
        return getHost().getConfigManager().getSetting(Settings.COPY_MODE);
    }

    @Override
    protected void setupInventorySlots() {
        var cell = getHost().getSubInventory(ISegmentedInventory.CELLS);
        addSlot(
                new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.WORKBENCH_CELL, cell, 0),
                SlotSemantics.STORAGE_CELL);
    }

    @Override
    protected void setupConfig() {
        var inv = getConfigInventory().createMenuWrapper();

        for (int slot = 0; slot < 63; slot++) {
            addSlot(new CellPartitionSlot(inv, this, slot), SlotSemantics.CONFIG);
        }
    }

    @Override
    protected void setupUpgrades() {
        var upgradeInventory = new SupplierInternalInventory<>(this::getUpgrades);

        for (int i = 0; i < 8; i++) {
            var slot = new OptionalRestrictedInputSlot(
                    RestrictedInputSlot.PlacableItemType.UPGRADES, upgradeInventory, this, i, i, getPlayerInventory());
            addSlot(slot, SlotSemantics.UPGRADE);
        }
    }

    public ItemStack getWorkbenchItem() {
        return Objects.requireNonNull(getHost().getSubInventory(ISegmentedInventory.CELLS))
                .getStackInSlot(0);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        copyMode = getWorkBenchCopyMode();

        var cell = getHost().getCell();
        setFuzzyMode(cell != null ? cell.getFuzzyMode(getWorkbenchItem()) : FuzzyMode.IGNORE_ALL);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return idx < getUpgrades().size();
    }

    @Override
    public boolean isPartitionSlotEnabled(int idx) {
        var cell = getHost().getCell();

        if (cell != null && getCopyMode() == CopyMode.CLEAR_ON_REMOVE) {
            return idx < cell.getConfigInventory(getWorkbenchItem()).size();
        }

        return getCopyMode() == CopyMode.KEEP_ON_REMOVE;
    }

    @Override
    public void onServerDataSync(ShortSet updatedFields) {
        super.onServerDataSync(updatedFields);
        getHost().getConfigManager().putSetting(Settings.COPY_MODE, getCopyMode());
    }

    public void clear() {
        if (isClientSide()) {
            sendClientAction(CellWorkbenchMenu.ACTION_CLEAR);
        } else {
            getConfigInventory().clear();
            broadcastChanges();
        }
    }

    public void partition() {
        if (isClientSide()) {
            sendClientAction(CellWorkbenchMenu.ACTION_PARTITION);
        } else {
            var inv = getConfigInventory();
            var is = getWorkbenchItem();

            var cellInv = StorageCells.getCellInventory(is, null);

            if (cellInv != null) {
                var it = Iterators.transform(cellInv.getAvailableStacks().iterator(), Map.Entry::getKey);

                for (var x = 0; x < inv.size(); x++) {
                    if (it.hasNext()) {
                        inv.setStack(x, new GenericStack(it.next(), 0));
                    } else {
                        inv.setStack(x, null);
                    }
                }
            }

            broadcastChanges();
        }
    }

    private GenericStackInv getConfigInventory() {
        return Objects.requireNonNull(getHost().getConfig());
    }

    public CopyMode getCopyMode() {
        return copyMode;
    }
}

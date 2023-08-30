package gripe._90.megacells.item.cell;

import static gripe._90.megacells.definition.MEGAItems.COMPRESSION_CARD;

import java.math.BigInteger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import gripe._90.megacells.item.MEGABulkCell;
import gripe._90.megacells.util.CompressionChain;
import gripe._90.megacells.util.CompressionService;

public class BulkCellInventory implements StorageCell {
    private static final String KEY = "key";
    private static final String UNIT_COUNT = "smallestUnitCount";

    private final ISaveProvider container;
    private final ItemStack i;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final CompressionChain compressionChain;
    private BigInteger unitCount;
    private AEItemKey highestCompressed;
    private final long unitFactor;
    private final boolean compressionEnabled;

    private boolean isPersisted = true;

    public BulkCellInventory(MEGABulkCell cell, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.container = container;

        this.filterItem = (AEItemKey) cell.getConfigInventory(i).getKey(0);
        this.compressionChain = CompressionService.getChain(filterItem).orElseGet(CompressionChain::new);
        this.unitFactor = compressionChain.unitFactor(filterItem);

        this.storedItem = getTag().contains(KEY) ? AEItemKey.fromTag(getTag().getCompound(KEY)) : null;
        this.unitCount = !getTag().getString(UNIT_COUNT).isEmpty()
                ? new BigInteger(getTag().getString(UNIT_COUNT))
                : BigInteger.ZERO;
        this.highestCompressed = storedItem;

        this.compressionEnabled = cell.getUpgrades(i).isInstalled(COMPRESSION_CARD);
    }

    private long clampedLong(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    private CompoundTag getTag() {
        return i.getOrCreateTag();
    }

    @Override
    public CellState getStatus() {
        if (unitCount.signum() == 0) {
            return CellState.EMPTY;
        }

        if (!storedItem.equals(filterItem)) {
            return CellState.FULL;
        }

        return CellState.NOT_EMPTY;
    }

    public AEItemKey getStoredItem() {
        return storedItem;
    }

    public long getStoredQuantity() {
        return clampedLong(unitCount.divide(BigInteger.valueOf(unitFactor)), Long.MAX_VALUE);
    }

    public AEItemKey getFilterItem() {
        return filterItem;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public CompressionChain getDecompressionChain() {
        return compressionChain.decompressFrom(highestCompressed);
    }

    @Override
    public double getIdleDrain() {
        return 10.0f;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !(what instanceof AEItemKey item)) {
            return 0;
        }

        if (!compressionEnabled && (!filterItem.equals(what) || storedItem != null && !storedItem.equals(what))) {
            return 0;
        }

        if (compressionEnabled && !filterItem.equals(what) && !compressionChain.containsVariant(item)) {
            return 0;
        }

        var factor = BigInteger.valueOf(compressionChain.unitFactor(item));
        var units = BigInteger.valueOf(amount).multiply(factor);

        if (mode == Actionable.MODULATE) {
            if (storedItem == null) {
                storedItem = filterItem;
            }

            unitCount = unitCount.add(units);
            saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (unitCount.signum() < 1 || !(what instanceof AEItemKey item)) {
            return 0;
        }

        var itemCount = unitCount.divide(BigInteger.valueOf(unitFactor));
        if (!compressionEnabled && (itemCount.signum() < 1 || !storedItem.equals(what))) {
            return 0;
        }

        if (compressionEnabled
                && !storedItem.equals(what)
                && !filterItem.equals(what)
                && !compressionChain.containsVariant(item)) {
            return 0;
        }

        var factor = BigInteger.valueOf(compressionChain.unitFactor(item));
        var units = BigInteger.valueOf(amount).multiply(factor);
        var currentUnitCount = unitCount;

        if (currentUnitCount.compareTo(units) <= 0) {
            if (mode == Actionable.MODULATE) {
                storedItem = null;
                unitCount = BigInteger.ZERO;
                saveChanges();
            }

            return clampedLong(currentUnitCount.divide(factor), Long.MAX_VALUE);
        } else {
            if (mode == Actionable.MODULATE) {
                unitCount = unitCount.subtract(units);
                saveChanges();
            }

            return clampedLong(units.divide(factor), Long.MAX_VALUE);
        }
    }

    private void saveChanges() {
        isPersisted = false;

        if (container != null) {
            container.saveChanges();
        } else {
            // if there is no ISaveProvider, store to NBT immediately
            persist();
        }
    }

    @Override
    public void persist() {
        if (isPersisted) {
            return;
        }

        if (storedItem == null || unitCount.signum() == -1) {
            getTag().remove(KEY);
            getTag().remove(UNIT_COUNT);
        } else {
            getTag().put(KEY, storedItem.toTagGeneric());
            getTag().putString(UNIT_COUNT, unitCount.toString());
        }

        isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (storedItem != null) {
            var stackLimit = (long) Math.pow(2, 42);

            if (compressionEnabled && storedItem.equals(filterItem)) {
                var count = unitCount;
                var chain = compressionChain.lastMultiplierSwapped();

                for (var variant : chain) {
                    var compressionFactor = BigInteger.valueOf(variant.factor());
                    var key = variant.item();

                    if (count.divide(compressionFactor).signum() == 1 && variant != chain.last()) {
                        out.add(key, clampedLong(count.remainder(compressionFactor), stackLimit));
                        count = count.divide(compressionFactor);
                    } else {
                        out.add(key, clampedLong(count, stackLimit));
                        highestCompressed = key;
                        break;
                    }
                }
            } else {
                out.add(storedItem, clampedLong(unitCount.divide(BigInteger.valueOf(unitFactor)), stackLimit));
            }
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return what instanceof AEItemKey item && (filterItem.equals(what) || compressionChain.containsVariant(item));
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}

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

import gripe._90.megacells.util.CompressionChain;
import gripe._90.megacells.util.CompressionService;

public class BulkCellInventory implements StorageCell {
    private static final String KEY = "key";
    private static final String UNIT_COUNT = "smallestUnitCount";
    private static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final ISaveProvider container;
    private final ItemStack stack;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final boolean compressionEnabled;
    private final CompressionChain compressionChain;
    private BigInteger unitCount;
    private final BigInteger unitFactor;

    private boolean isPersisted = true;

    BulkCellInventory(ItemStack stack, ISaveProvider container) {
        this.stack = stack;
        this.container = container;

        var cell = (BulkCellItem) stack.getItem();
        var filter = cell.getConfigInventory(this.stack).getKey(0);
        filterItem = filter instanceof AEItemKey item ? item : null;

        storedItem = getTag().contains(KEY) ? AEItemKey.fromTag(getTag().getCompound(KEY)) : null;
        unitCount = !getTag().getString(UNIT_COUNT).isEmpty()
                ? new BigInteger(getTag().getString(UNIT_COUNT))
                : BigInteger.ZERO;

        compressionEnabled = cell.getUpgrades(this.stack).isInstalled(COMPRESSION_CARD);
        compressionChain = CompressionService.INSTANCE
                .getChain(storedItem != null ? storedItem : filterItem)
                .orElseGet(CompressionChain::new);
        unitFactor = compressionChain.unitFactor(storedItem != null ? storedItem : filterItem);
    }

    private long clampedLong(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    private CompoundTag getTag() {
        return stack.getOrCreateTag();
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
        return clampedLong(unitCount.divide(unitFactor), Long.MAX_VALUE);
    }

    public AEItemKey getFilterItem() {
        return filterItem;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public CompressionChain getCompressionChain() {
        return compressionChain;
    }

    @Override
    public double getIdleDrain() {
        return 5.0f;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !(what instanceof AEItemKey item)) {
            return 0;
        }

        if (filterItem == null || (storedItem != null && !filterItem.equals(storedItem))) {
            return 0;
        }

        if (!compressionEnabled && !what.equals(filterItem)) {
            return 0;
        }

        if (compressionEnabled && !compressionChain.containsVariant(item)) {
            return 0;
        }

        var factor = compressionChain.unitFactor(item);
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

        if (filterItem == null || (storedItem != null && !filterItem.equals(storedItem))) {
            return 0;
        }

        if (!compressionEnabled && (unitCount.divide(unitFactor).signum() < 1 || !what.equals(storedItem))) {
            return 0;
        }

        if (compressionEnabled && !compressionChain.containsVariant(item)) {
            return 0;
        }

        var factor = compressionChain.unitFactor(item);
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

        if (storedItem == null || unitCount.signum() < 1) {
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
            if (compressionEnabled && storedItem.equals(filterItem) && !compressionChain.isEmpty()) {
                var count = unitCount;
                var chain = compressionChain.limited().lastMultiplierSwapped();

                for (var variant : chain) {
                    var compressionFactor = BigInteger.valueOf(variant.factor());
                    var key = variant.item();

                    if (count.divide(compressionFactor).signum() == 1 && variant != chain.last()) {
                        out.add(key, count.remainder(compressionFactor).longValue());
                        count = count.divide(compressionFactor);
                    } else {
                        out.add(key, clampedLong(count, STACK_LIMIT));
                        break;
                    }
                }
            } else {
                out.add(storedItem, clampedLong(unitCount.divide(unitFactor), STACK_LIMIT));
            }
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return what instanceof AEItemKey item
                && (item.equals(storedItem) || item.equals(filterItem) || compressionChain.containsVariant(item));
    }

    @Override
    public boolean canFitInsideCell() {
        return filterItem == null && storedItem == null && unitCount.signum() < 1;
    }

    @Override
    public Component getDescription() {
        return stack.getHoverName();
    }
}

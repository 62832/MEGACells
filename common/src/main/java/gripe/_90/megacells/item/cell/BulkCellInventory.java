package gripe._90.megacells.item.cell;

import static gripe._90.megacells.definition.MEGAItems.COMPRESSION_CARD;

import java.math.BigInteger;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

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
import gripe._90.megacells.item.part.DecompressionModulePart;
import gripe._90.megacells.util.CompressionChain;
import gripe._90.megacells.util.CompressionService;

public class BulkCellInventory implements StorageCell {
    private static final String KEY = "key";
    private static final String UNIT_COUNT = "smallestUnitCount";
    private static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final ISaveProvider container;
    private final ItemStack i;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final boolean compressionEnabled;
    private final CompressionChain compressionChain;
    private BigInteger unitCount;
    private final BigInteger unitFactor;

    private Object2LongMap<AEItemKey> availableVariants;
    // Cache used by the decompression service in order for auto-crafting to not break and hang indefinitely.
    private ObjectSet<AEItemKey> cachedVariants;

    private boolean isPersisted = true;

    public BulkCellInventory(MEGABulkCell cell, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.container = container;

        filterItem = (AEItemKey) cell.getConfigInventory(i).getKey(0);
        compressionEnabled = cell.getUpgrades(i).isInstalled(COMPRESSION_CARD);
        compressionChain = CompressionService.getChain(filterItem).orElseGet(CompressionChain::new);
        unitFactor = compressionChain.unitFactor(filterItem);

        storedItem = getTag().contains(KEY) ? AEItemKey.fromTag(getTag().getCompound(KEY)) : null;
        unitCount = !getTag().getString(UNIT_COUNT).isEmpty()
                ? new BigInteger(getTag().getString(UNIT_COUNT))
                : BigInteger.ZERO;

        updateVariants(true);
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
        return clampedLong(unitCount.divide(unitFactor), Long.MAX_VALUE);
    }

    public AEItemKey getFilterItem() {
        return filterItem;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public CompressionChain getDecompressionChain() {
        if (cachedVariants.isEmpty()) {
            return new CompressionChain();
        }

        var highest = cachedVariants.stream().toList().get(0);
        return compressionChain.decompressFrom(highest);
    }

    private void updateVariants(boolean cache) {
        availableVariants = gatherVariants();

        if (cache) {
            cachedVariants = availableVariants.keySet();
        }
    }

    private Object2LongMap<AEItemKey> gatherVariants() {
        var variants = new Object2LongLinkedOpenHashMap<AEItemKey>();

        if (storedItem != null) {
            if (compressionEnabled && storedItem.equals(filterItem) && !compressionChain.isEmpty()) {
                var count = unitCount;
                var chain = compressionChain.lastMultiplierSwapped();

                for (var variant : chain) {
                    var compressionFactor = BigInteger.valueOf(variant.factor());
                    var key = variant.item();

                    if (count.divide(compressionFactor).signum() == 1 && variant != chain.last()) {
                        variants.putAndMoveToFirst(
                                key, count.remainder(compressionFactor).longValue());
                        count = count.divide(compressionFactor);
                    } else {
                        variants.putAndMoveToFirst(key, clampedLong(count, STACK_LIMIT));
                        break;
                    }
                }
            } else {
                variants.put(storedItem, clampedLong(unitCount.divide(unitFactor), STACK_LIMIT));
            }
        }

        return variants;
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

        if (!compressionEnabled && (!filterItem.equals(what) || storedItem != null && !storedItem.equals(what))) {
            return 0;
        }

        if (compressionEnabled && !filterItem.equals(what) && !compressionChain.containsVariant(item)) {
            return 0;
        }

        var factor = compressionChain.unitFactor(item);
        var units = BigInteger.valueOf(amount).multiply(factor);

        if (mode == Actionable.MODULATE) {
            if (storedItem == null) {
                storedItem = filterItem;
            }

            unitCount = unitCount.add(units);
            updateVariants(source.machine().isPresent() && source.machine().get() instanceof DecompressionModulePart);
            saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (unitCount.signum() < 1 || !(what instanceof AEItemKey item)) {
            return 0;
        }

        var itemCount = unitCount.divide(unitFactor);
        if (!compressionEnabled && (itemCount.signum() < 1 || !storedItem.equals(what))) {
            return 0;
        }

        if (compressionEnabled
                && !storedItem.equals(what)
                && !filterItem.equals(what)
                && !compressionChain.containsVariant(item)) {
            return 0;
        }

        var factor = compressionChain.unitFactor(item);
        var units = BigInteger.valueOf(amount).multiply(factor);
        var currentUnitCount = unitCount;

        if (currentUnitCount.compareTo(units) <= 0) {
            if (mode == Actionable.MODULATE) {
                storedItem = null;
                unitCount = BigInteger.ZERO;
                updateVariants(false);
                saveChanges();
            }

            return clampedLong(currentUnitCount.divide(factor), Long.MAX_VALUE);
        } else {
            if (mode == Actionable.MODULATE) {
                unitCount = unitCount.subtract(units);
                updateVariants(false);
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
        availableVariants.forEach(out::add);
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

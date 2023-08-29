package gripe._90.megacells.item.cell;

import static gripe._90.megacells.definition.MEGAItems.COMPRESSION_CARD;

import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

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
import appeng.util.prioritylist.IPartitionList;

import gripe._90.megacells.item.MEGABulkCell;
import gripe._90.megacells.service.CompressionService;

public class BulkCellInventory implements StorageCell {
    private static final String KEY = "key";
    private static final String UNIT_COUNT = "smallestUnitCount";

    private final ISaveProvider container;
    private final ItemStack i;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;
    private final IPartitionList partitionList;

    private final Object2IntMap<AEItemKey> compressed;
    private final Object2IntMap<AEItemKey> decompressed;
    private BigInteger unitCount;
    private AEItemKey highestCompressed;
    private final long unitFactor;
    private final boolean compressionEnabled;

    private boolean isPersisted = true;

    public BulkCellInventory(MEGABulkCell cell, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.container = container;

        var config = cell.getConfigInventory(i);
        this.filterItem = (AEItemKey) config.getKey(0);

        var builder = IPartitionList.builder();
        builder.addAll(config.keySet());
        this.partitionList = builder.build();

        this.compressed = CompressionService.getVariants(filterItem, false);
        this.decompressed = CompressionService.getVariants(filterItem, true);
        this.unitFactor = decompressed.values().intStream().asLongStream().reduce(1, Math::multiplyExact);

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

    public AEItemKey getHighestCompressed() {
        return highestCompressed;
    }

    public AEItemKey getFilterItem() {
        return filterItem;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
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

        if (!compressionEnabled && (!partitionList.isListed(what) || storedItem != null && !storedItem.equals(what))) {
            return 0;
        }

        if (compressionEnabled
                && !partitionList.isListed(what)
                && !compressed.containsKey(item)
                && !decompressed.containsKey(item)) {
            return 0;
        }

        var units = BigInteger.valueOf(amount).multiply(compressedTransferFactor(item));

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
                && !compressed.containsKey(item)
                && !decompressed.containsKey(item)) {
            return 0;
        }

        var extractionFactor = compressedTransferFactor(item);
        var units = BigInteger.valueOf(amount).multiply(extractionFactor);
        var currentUnitCount = unitCount;

        if (currentUnitCount.compareTo(units) <= 0) {
            if (mode == Actionable.MODULATE) {
                storedItem = null;
                unitCount = BigInteger.ZERO;
                saveChanges();
            }
            return clampedLong(currentUnitCount.divide(extractionFactor), Long.MAX_VALUE);
        } else {
            if (mode == Actionable.MODULATE) {
                unitCount = unitCount.subtract(units);
                saveChanges();
            }
            return clampedLong(units.divide(extractionFactor), Long.MAX_VALUE);
        }
    }

    private BigInteger compressedTransferFactor(AEItemKey what) {
        if (compressed.getInt(what) > 0) {
            return compressedTransferFactor(compressed, unitFactor, keys -> Pair.of(0, keys.indexOf(what) + 1));
        } else if (decompressed.getInt(what) > 0) {
            return compressedTransferFactor(decompressed, 1, keys -> Pair.of(keys.indexOf(what) + 1, keys.size()));
        } else {
            return BigInteger.valueOf(unitFactor);
        }
    }

    private BigInteger compressedTransferFactor(
            Object2IntMap<AEItemKey> variants, long baseFactor, Function<List<?>, Pair<Integer, Integer>> subLister) {
        var variantKeys = new LinkedList<>(variants.keySet());
        var toStored = new Object2IntLinkedOpenHashMap<>(variants);

        var range = subLister.apply(variantKeys);
        toStored.keySet().retainAll(variantKeys.subList(range.first(), range.second()));

        for (var i : toStored.values()) {
            baseFactor *= i;
        }

        return BigInteger.valueOf(baseFactor);
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
                var allVariants = new Object2IntLinkedOpenHashMap<AEItemKey>();

                if (!decompressed.isEmpty()) {
                    var decompressedKeys = new LinkedList<>(decompressed.keySet());
                    Collections.reverse(decompressedKeys);
                    decompressedKeys.forEach(k -> allVariants.put(k, decompressed.getInt(k)));

                    allVariants.put(storedItem, decompressed.getInt(decompressedKeys.getLast()));
                    allVariants.putAll(compressed);
                } else if (!compressed.isEmpty()) {
                    allVariants.put(
                            storedItem,
                            compressed.values().intStream().findFirst().orElseThrow());
                    allVariants.putAll(compressed);
                } else {
                    allVariants.put(storedItem, 1);
                }

                var count = unitCount;

                for (var variant : allVariants.keySet()) {
                    var compressionFactor = BigInteger.valueOf(allVariants.getInt(variant));

                    if (count.divide(compressionFactor).signum() == 1 && variant != allVariants.lastKey()) {
                        out.add(variant, clampedLong(count.remainder(compressionFactor), stackLimit));
                        count = count.divide(compressionFactor);
                    } else {
                        out.add(variant, clampedLong(count, stackLimit));
                        highestCompressed = variant;
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
        return what instanceof AEItemKey item
                && (partitionList.isListed(item) || compressed.containsKey(item) || decompressed.containsKey(item));
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}

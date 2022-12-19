package gripe._90.megacells.item.cell;

import static gripe._90.megacells.definition.MEGAItems.COMPRESSION_CARD;

import java.math.BigInteger;
import java.util.ArrayList;

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
    private final long unitFactor;
    protected final boolean compressionEnabled;

    private boolean isPersisted = true;

    public BulkCellInventory(MEGABulkCell cell, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.container = container;

        var config = cell.getConfigInventory(i);
        this.filterItem = (AEItemKey) config.getKey(0);

        var builder = IPartitionList.builder();
        builder.addAll(config.keySet());
        this.partitionList = builder.build();

        this.compressed = CompressionHandler.INSTANCE.getCompressedVariants(filterItem);
        this.decompressed = CompressionHandler.INSTANCE.getDecompressedVariants(filterItem);
        this.unitFactor = decompressed.values().intStream().reduce(1, Math::multiplyExact);

        this.storedItem = getTag().contains(KEY) ? AEItemKey.fromTag(getTag().getCompound(KEY)) : null;
        this.unitCount = retrieveUnitCount();

        this.compressionEnabled = cell.getUpgrades(i).isInstalled(COMPRESSION_CARD);
    }

    private BigInteger retrieveUnitCount() {
        // TODO 1.19.3 / 1.20.0: Remove pre-2.0.0 bulk cell conversion (again)
        if (getTag().contains("count")) {
            return BigInteger.valueOf(getTag().getLong("count")).multiply(BigInteger.valueOf(unitFactor));
        } else {
            return !getTag().getString(UNIT_COUNT).equals("")
                    ? new BigInteger(getTag().getString(UNIT_COUNT))
                    : BigInteger.ZERO;
        }
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

    protected AEKey getStoredItem() {
        return storedItem;
    }

    protected AEKey getFilterItem() {
        return filterItem;
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

        if (compressionEnabled && !partitionList.isListed(what)
                && !compressed.containsKey(item) && !decompressed.containsKey(item)) {
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

        if (compressionEnabled && !storedItem.equals(what)
                && !compressed.containsKey(item) && !decompressed.containsKey(item)) {
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
        // FIXME: this currently throws int overflows due to fastutil's default iterator implementation
        if (compressed.getInt(what) > 0) {
            var variantKeys = new ArrayList<>(compressed.keySet());
            var toDecompress = new Object2IntLinkedOpenHashMap<>(compressed);
            toDecompress.keySet().retainAll(variantKeys.subList(0, variantKeys.indexOf(what) + 1));
            return BigInteger.valueOf(toDecompress.values().intStream().reduce(1, Math::multiplyExact))
                    .multiply(BigInteger.valueOf(unitFactor));
        } else if (decompressed.getInt(what) > 0) {
            var variantKeys = new ArrayList<>(decompressed.keySet());
            var toCompress = new Object2IntLinkedOpenHashMap<>(decompressed);
            toCompress.keySet().retainAll(variantKeys.subList(variantKeys.indexOf(what) + 1, variantKeys.size()));
            return BigInteger.valueOf(toCompress.values().intStream().reduce(1, Math::multiplyExact));
        } else {
            return BigInteger.valueOf(unitFactor);
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

        // remove pre-2.0.0 count tag
        getTag().remove("count");

        isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        var stackLimit = (long) Math.pow(2, 42);
        if (storedItem != null) {
            out.add(storedItem, clampedLong(unitCount.divide(BigInteger.valueOf(unitFactor)), stackLimit));
            getVariantStacks(out);
        }
    }

    public void getVariantStacks(KeyCounter out) {
        var stackLimit = (long) Math.pow(2, 42);
        if (compressionEnabled && storedItem.equals(filterItem)) {
            var compressionFactor = unitFactor;
            for (var variant : compressed.keySet()) {
                compressionFactor *= compressed.getInt(variant);
                var count = unitCount.divide(BigInteger.valueOf(compressionFactor));

                if (count.signum() == 1) {
                    out.add(variant, clampedLong(count, stackLimit));
                }
            }

            if (!decompressed.isEmpty()) {
                var keys = new ArrayList<>(decompressed.keySet());
                var baseUnit = keys.get(keys.size() - 1);
                out.add(baseUnit, clampedLong(unitCount, stackLimit));

                var decompressionFactor = 1;
                for (var variant : keys) {
                    decompressionFactor *= decompressed.getInt(variant);
                    var count = unitCount.divide(BigInteger.valueOf(decompressionFactor));
                    out.add(variant, clampedLong(count, stackLimit));
                }
            }
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        if (!(what instanceof AEItemKey item)) {
            return false;
        }
        return partitionList.isListed(item) || compressed.containsKey(item) || decompressed.containsKey(item);
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}

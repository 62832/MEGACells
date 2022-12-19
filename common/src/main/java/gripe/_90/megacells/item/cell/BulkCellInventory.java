package gripe._90.megacells.item.cell;

import java.math.BigInteger;
import java.util.List;

import it.unimi.dsi.fastutil.Pair;

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
import appeng.api.upgrades.IUpgradeInventory;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.MEGABulkCell;

public class BulkCellInventory implements StorageCell {
    private static final String KEY = "key";
    private static final String UNIT_COUNT = "smallestUnitCount";

    private final ISaveProvider container;
    private final ItemStack i;
    private final MEGABulkCell cell;

    private AEKey storedItem;
    private final IPartitionList partitionList;

    private final List<Pair<AEItemKey, Integer>> compressed;
    private final List<Pair<AEItemKey, Integer>> decompressed;
    private BigInteger unitCount;
    private final long unitFactor;
    private final boolean compressionEnabled;

    private boolean isPersisted = true;

    public BulkCellInventory(MEGABulkCell cell, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.cell = cell;
        this.container = container;

        var builder = IPartitionList.builder();
        var config = getConfigInventory();
        builder.addAll(config.keySet());
        this.partitionList = builder.build();

        this.compressed = CompressionHandler.INSTANCE.getCompressedVariants(getFilterItem());
        this.decompressed = CompressionHandler.INSTANCE.getDecompressedVariants(getFilterItem());
        this.unitFactor = this.decompressed.stream().mapToInt(Pair::second).reduce(1, Math::multiplyExact);

        this.storedItem = getTag().contains(KEY) ? AEKey.fromTagGeneric(getTag().getCompound(KEY)) : null;
        this.unitCount = retrieveUnitCount();

        this.compressionEnabled = getUpgrades().isInstalled(MEGAItems.COMPRESSION_CARD);
    }

    private BigInteger retrieveUnitCount() {
        // TODO 1.19.3 / 1.20.0: Remove pre-2.0.0 bulk cell conversion (again)
        if (getTag().contains("count")) {
            return BigInteger.valueOf(getTag().getLong("count")).multiply(BigInteger.valueOf(this.unitFactor));
        } else {
            return !getTag().getString(UNIT_COUNT).equals("")
                    ? new BigInteger(getTag().getString(UNIT_COUNT))
                    : BigInteger.ZERO;
        }
    }

    private long clampedLong(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    private boolean isCompressedVariant(AEKey key) {
        return this.compressed.stream().anyMatch(p -> p.first().equals(key));
    }

    private boolean isDecompressedVariant(AEKey key) {
        return this.decompressed.stream().anyMatch(p -> p.first().equals(key));
    }

    private CompoundTag getTag() {
        return this.i.getOrCreateTag();
    }

    @Override
    public CellState getStatus() {
        if (this.unitCount.signum() == 0) {
            return CellState.EMPTY;
        }
        if (!this.storedItem.equals(getFilterItem())) {
            return CellState.FULL;
        }
        return CellState.NOT_EMPTY;
    }

    protected AEKey getStoredItem() {
        return this.storedItem;
    }

    protected AEKey getFilterItem() {
        var config = getConfigInventory().keySet().stream().toList();
        if (config.isEmpty()) {
            return null;
        } else {
            return config.get(0);
        }
    }

    @Override
    public double getIdleDrain() {
        return 10.0f;
    }

    public ConfigInventory getConfigInventory() {
        return this.cell.getConfigInventory(this.i);
    }

    public IUpgradeInventory getUpgrades() {
        return this.cell.getUpgrades(this.i);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0) {
            return 0;
        }

        if (!this.compressionEnabled && (!this.partitionList.isListed(what)
                || this.storedItem != null && !this.storedItem.equals(what))) {
            return 0;
        }

        if (this.compressionEnabled && !this.partitionList.isListed(what)
                && !isCompressedVariant(what) && !isDecompressedVariant(what)) {
            return 0;
        }

        var units = BigInteger.valueOf(amount).multiply(compressedTransferFactor(what));

        if (mode == Actionable.MODULATE) {
            if (this.storedItem == null) {
                this.storedItem = getFilterItem();
            }
            this.unitCount = this.unitCount.add(units);
            saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (this.unitCount.signum() < 1) {
            return 0;
        }

        var itemCount = this.unitCount.divide(BigInteger.valueOf(this.unitFactor));
        if (!this.compressionEnabled && (itemCount.signum() < 1 || !this.storedItem.equals(what))) {
            return 0;
        }

        if (this.compressionEnabled && !this.storedItem.equals(what)
                && !isCompressedVariant(what) && !isDecompressedVariant(what)) {
            return 0;
        }

        var extractionFactor = compressedTransferFactor(what);
        var units = BigInteger.valueOf(amount).multiply(extractionFactor);
        var currentUnitCount = this.unitCount;

        if (currentUnitCount.compareTo(units) <= 0) {
            if (mode == Actionable.MODULATE) {
                this.storedItem = null;
                this.unitCount = BigInteger.ZERO;
                saveChanges();
            }
            return clampedLong(currentUnitCount.divide(extractionFactor), Long.MAX_VALUE);
        } else {
            if (mode == Actionable.MODULATE) {
                this.unitCount = this.unitCount.subtract(units);
                saveChanges();
            }
            return clampedLong(units.divide(extractionFactor), Long.MAX_VALUE);
        }
    }

    private BigInteger compressedTransferFactor(AEKey what) {
        var compressedTransfer = this.compressed.stream().filter(p -> p.first().equals(what)).findFirst();
        var decompressedTransfer = this.decompressed.stream().filter(p -> p.first().equals(what)).findFirst();

        if (compressedTransfer.isPresent()) {
            var toDecompress = this.compressed
                    .subList(0, this.compressed.indexOf(compressedTransfer.get()) + 1);
            return BigInteger.valueOf(toDecompress.stream().mapToLong(Pair::second)
                    .reduce(1, Math::multiplyExact)).multiply(BigInteger.valueOf(this.unitFactor));
        } else if (decompressedTransfer.isPresent()) {
            var toCompress = this.decompressed
                    .subList(this.decompressed.indexOf(decompressedTransfer.get()) + 1, this.decompressed.size());
            return BigInteger.valueOf(toCompress.stream().mapToInt(Pair::second)
                    .reduce(1, Math::multiplyExact));
        } else {
            return BigInteger.valueOf(this.unitFactor);
        }
    }

    protected void saveChanges() {
        this.isPersisted = false;
        if (this.container != null) {
            this.container.saveChanges();
        } else {
            // if there is no ISaveProvider, store to NBT immediately
            this.persist();
        }
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }

        if (this.storedItem == null || this.unitCount.signum() == -1) {
            this.getTag().remove(KEY);
            this.getTag().remove(UNIT_COUNT);
        } else {
            this.getTag().put(KEY, this.storedItem.toTagGeneric());
            this.getTag().putString(UNIT_COUNT, this.unitCount.toString());
        }

        // remove pre-2.0.0 count tag
        getTag().remove("count");

        this.isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        var stackLimit = (long) Math.pow(2, 42);
        if (this.storedItem != null) {
            out.add(this.storedItem, clampedLong(
                    this.unitCount.divide(BigInteger.valueOf(this.unitFactor)),
                    stackLimit));
            getVariantStacks(out);
        }
    }

    public void getVariantStacks(KeyCounter out) {
        var stackLimit = (long) Math.pow(2, 42);
        if (this.compressionEnabled && this.storedItem.equals(this.getFilterItem())) {
            var compressionFactor = this.unitFactor;
            for (var variant : this.compressed) {
                compressionFactor *= variant.second();
                var count = this.unitCount.divide(BigInteger.valueOf(compressionFactor));
                if (count.signum() == 1) {
                    out.add(variant.first(), clampedLong(count, stackLimit));
                }
            }

            if (!this.decompressed.isEmpty()) {
                var baseUnit = this.decompressed.get(this.decompressed.size() - 1);
                out.add(baseUnit.first(), clampedLong(this.unitCount, stackLimit));

                var decompressionFactor = 1;
                for (int i = this.decompressed.size() - 2; i >= 0; i--) {
                    var variant = this.decompressed.get(i);
                    decompressionFactor *= variant.second();
                    var count = this.unitCount.divide(BigInteger.valueOf(decompressionFactor));
                    out.add(variant.first(), clampedLong(count, stackLimit));
                }
            }
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return this.partitionList.isListed(what) || isCompressedVariant(what) || isDecompressedVariant(what);
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}

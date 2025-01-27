package gripe._90.megacells.item.cell;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.misc.CompressionChain;
import gripe._90.megacells.misc.CompressionService;
import gripe._90.megacells.misc.DecompressionPattern;

public class BulkCellInventory implements StorageCell {
    public static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final ISaveProvider container;
    private final ItemStack stack;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final boolean compressionEnabled;
    private final CompressionChain compressionChain;
    private BigInteger unitCount;
    private final BigInteger unitFactor;
    private final int compressionCutoff;
    private Set<IPatternDetails> decompressionPatterns;

    private boolean isPersisted = true;

    BulkCellInventory(ItemStack stack, ISaveProvider container) {
        this.stack = stack;
        this.container = container;

        var cell = (BulkCellItem) stack.getItem();
        filterItem = (AEItemKey) cell.getConfigInventory(stack).getKey(0);

        storedItem = (AEItemKey) stack.get(MEGAComponents.BULK_CELL_ITEM);
        unitCount = stack.getOrDefault(MEGAComponents.BULK_CELL_UNIT_COUNT, BigInteger.ZERO);

        compressionEnabled = cell.getUpgrades(stack).isInstalled(MEGAItems.COMPRESSION_CARD);
        compressionChain = CompressionService.getChain(storedItem != null ? storedItem : filterItem);
        unitFactor = compressionChain.unitFactor(storedItem != null ? storedItem : filterItem);

        // Check newly-calculated factor against what's already recorded in order to adjust for a compression chain that
        // has changed from the left of the stored item
        var recordedFactor = stack.getOrDefault(MEGAComponents.BULK_CELL_UNIT_FACTOR, unitFactor);

        if (!unitFactor.equals(recordedFactor)) {
            unitCount = unitCount.multiply(unitFactor).divide(recordedFactor);
            saveChanges();
        }

        int recordedCutoff = stack.getOrDefault(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF, compressionChain.size());

        if (recordedCutoff > compressionChain.size()) {
            stack.remove(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF);
            compressionCutoff = compressionChain.size();
        } else {
            compressionCutoff = recordedCutoff;
        }
    }

    private long clampedLong(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    @Override
    public CellState getStatus() {
        if (storedItem == null || unitCount.signum() < 1) {
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

    public Set<IPatternDetails> getDecompressionPatterns() {
        if (!compressionEnabled || compressionChain.isEmpty()) {
            return Set.of();
        }

        if (decompressionPatterns == null) {
            var decompressionChain = compressionChain.limited(compressionCutoff).reversed();
            decompressionPatterns = new ObjectLinkedOpenHashSet<>();

            for (var variant : decompressionChain) {
                if (variant == decompressionChain.getLast()) {
                    continue;
                }

                var decompressed = decompressionChain.get(decompressionChain.indexOf(variant) + 1);
                decompressionPatterns.add(new DecompressionPattern(decompressed.item(), variant, false));
            }

            var remainingChain = compressionChain.trailing(decompressionChain.size() - 1);

            for (var variant : remainingChain) {
                if (variant == remainingChain.getFirst()) {
                    continue;
                }

                var decompressed = remainingChain.get(remainingChain.indexOf(variant) - 1);
                decompressionPatterns.add(new DecompressionPattern(decompressed.item(), variant, true));
            }
        }

        return Collections.unmodifiableSet(decompressionPatterns);
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

        if (compressionEnabled && !what.equals(filterItem) && !compressionChain.containsVariant(item.getItem())) {
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

        if (compressionEnabled && !what.equals(filterItem) && !compressionChain.containsVariant(item.getItem())) {
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
            persist();
        }
    }

    @Override
    public void persist() {
        if (isPersisted) {
            return;
        }

        if (storedItem == null || unitCount.signum() < 1) {
            stack.remove(MEGAComponents.BULK_CELL_ITEM);
            stack.remove(MEGAComponents.BULK_CELL_UNIT_COUNT);
            stack.remove(MEGAComponents.BULK_CELL_UNIT_FACTOR);
        } else {
            stack.set(MEGAComponents.BULK_CELL_ITEM, storedItem);
            stack.set(MEGAComponents.BULK_CELL_UNIT_COUNT, unitCount);
            stack.set(MEGAComponents.BULK_CELL_UNIT_FACTOR, unitFactor);
        }

        isPersisted = true;
    }

    public void setCompressionCutoff(int cutoff) {
        if (cutoff == compressionChain.size()) {
            stack.remove(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF);
        } else {
            stack.set(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF, cutoff);
        }
    }

    public int getCompressionCutoff() {
        return compressionCutoff;
    }

    public Item getCutoffItem() {
        return compressionChain.getCutoffItem(compressionCutoff);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (storedItem != null) {
            if (compressionEnabled && storedItem.equals(filterItem) && !compressionChain.isEmpty()) {
                var count = unitCount;
                var chain = compressionChain.lastMultiplierSwapped(compressionCutoff);

                for (var variant : chain) {
                    var compressionFactor = BigInteger.valueOf(variant.factor());
                    var key = AEItemKey.of(variant.item());

                    if (count.divide(compressionFactor).signum() == 1 && variant != chain.getLast()) {
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
                && (item.equals(storedItem)
                        || item.equals(filterItem)
                        || compressionChain.containsVariant(item.getItem()));
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

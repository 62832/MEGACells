package gripe._90.megacells.item.cell;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import net.minecraft.network.chat.Component;
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

public class BulkCellInventory implements StorageCell {
    private final ISaveProvider container;
    private final ItemStack stack;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final boolean compressionEnabled;
    private CompressionChain compressionChain;
    private BigInteger unitCount;
    private BigInteger unitFactor;
    private int compressionCutoff;

    private Map<AEItemKey, Long> compressedStacks;
    private boolean needsStackUpdate;
    private List<IPatternDetails> decompressionPatterns;

    private boolean isPersisted = true;

    BulkCellInventory(ItemStack stack, ISaveProvider container) {
        this.stack = stack;
        this.container = container;

        var cell = (BulkCellItem) stack.getItem();
        filterItem = (AEItemKey) cell.getConfigInventory(stack).getKey(0);
        compressionEnabled = cell.getUpgrades(stack).isInstalled(MEGAItems.COMPRESSION_CARD);

        storedItem = (AEItemKey) stack.get(MEGAComponents.BULK_CELL_ITEM);
        unitCount = stack.getOrDefault(MEGAComponents.BULK_CELL_UNIT_COUNT, BigInteger.ZERO);

        var determiningItem = storedItem != null ? storedItem : filterItem;
        compressionChain = CompressionService.getChain(determiningItem);

        unitFactor = compressionChain.unitFactor(determiningItem);
        var recordedFactor = stack.getOrDefault(MEGAComponents.BULK_CELL_UNIT_FACTOR, unitFactor);

        if (!unitFactor.equals(recordedFactor)) {
            unitCount = unitCount.multiply(unitFactor).divide(recordedFactor);
            stack.set(MEGAComponents.BULK_CELL_UNIT_COUNT, unitCount);
            stack.set(MEGAComponents.BULK_CELL_UNIT_FACTOR, unitFactor);
        }

        int maxCutoff = Math.max(0, compressionChain.size() - 1);
        int recordedCutoff = stack.getOrDefault(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF, maxCutoff);
        compressionCutoff = recordedCutoff < 0 ? maxCutoff : Math.min(recordedCutoff, maxCutoff);

        compressedStacks = compressionChain.initStacks(unitCount, compressionCutoff, determiningItem);
    }

    @Override
    public CellState getStatus() {
        if (storedItem == null || unitCount.signum() < 1) {
            return CellState.EMPTY;
        }

        if (isFilterMismatched()) {
            return CellState.FULL;
        }

        return CellState.NOT_EMPTY;
    }

    AEItemKey getStoredItem() {
        return storedItem;
    }

    long getStoredQuantity() {
        return CompressionChain.clamp(unitCount.divide(unitFactor), Long.MAX_VALUE);
    }

    AEItemKey getFilterItem() {
        return filterItem;
    }

    private boolean isFilterMismatched() {
        if (storedItem == null) {
            return false;
        }

        if (storedItem.equals(filterItem)) {
            return false;
        }

        if (filterItem == null) {
            return true;
        }

        if (compressionChain.containsVariant(filterItem)) {
            storedItem = filterItem;
            unitFactor = compressionChain.unitFactor(storedItem);
            saveChanges();
            return false;
        }

        return true;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public boolean hasCompressionChain() {
        return !compressionChain.isEmpty();
    }

    long getTraceUnits() {
        return CompressionChain.clamp(unitCount.remainder(unitFactor), Long.MAX_VALUE);
    }

    public List<IPatternDetails> getDecompressionPatterns() {
        if (filterItem == null || !compressionEnabled || !hasCompressionChain() || isFilterMismatched()) {
            return List.of();
        }

        if (decompressionPatterns == null) {
            decompressionPatterns = compressionChain.getDecompressionPatterns(compressionCutoff);
        }

        return decompressionPatterns;
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

        if (isFilterMismatched()) {
            return 0;
        }

        if (!item.equals(filterItem) && (!compressionEnabled || !compressionChain.containsVariant(item))) {
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
            needsStackUpdate = true;
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (storedItem == null || unitCount.signum() < 1 || !(what instanceof AEItemKey item)) {
            return 0;
        }

        if (!compressionChain.containsVariant(item) && !item.equals(storedItem)) {
            return 0;
        }

        if (isFilterMismatched()) {
            amount = Math.min(amount, getAvailableStacks().get(item));
        } else if (!compressionEnabled && !item.equals(storedItem)) {
            return 0;
        }

        var factor = compressionChain.unitFactor(item);
        var units = BigInteger.valueOf(amount).multiply(factor).min(unitCount);

        if (mode == Actionable.MODULATE) {
            unitCount = unitCount.subtract(units).max(BigInteger.ZERO);

            if (unitCount.signum() < 1) {
                storedItem = null;
                var filterChain = CompressionService.getChain(filterItem);

                if (compressionChain != filterChain) {
                    compressionChain = filterChain;
                    compressionCutoff = Math.max(0, compressionChain.size() - 1);
                }
            }

            saveChanges();
            needsStackUpdate = true;
        }

        return CompressionChain.clamp(units.divide(factor), Long.MAX_VALUE);
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
            stack.remove(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF);
        } else {
            stack.set(MEGAComponents.BULK_CELL_ITEM, storedItem);
            stack.set(MEGAComponents.BULK_CELL_UNIT_COUNT, unitCount);
            stack.set(MEGAComponents.BULK_CELL_UNIT_FACTOR, unitFactor);
            stack.set(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF, compressionCutoff);
        }

        isPersisted = true;
    }

    public void switchCompressionCutoff(boolean backwards) {
        if (!hasCompressionChain()) {
            return;
        }

        var newCutoff = compressionCutoff;
        newCutoff += (backwards ? 1 : -1);
        newCutoff %= compressionChain.size();
        compressionCutoff = newCutoff;
        stack.set(MEGAComponents.BULK_CELL_COMPRESSION_CUTOFF, compressionCutoff);
        decompressionPatterns = null;
    }

    public ItemStack getCutoffItem() {
        return hasCompressionChain() ? compressionChain.getItem(compressionCutoff) : ItemStack.EMPTY;
    }

    ItemStack getHighestVariant() {
        return hasCompressionChain() ? compressionChain.getItem(compressionChain.size() - 1) : ItemStack.EMPTY;
    }

    ItemStack getLowestVariant() {
        return hasCompressionChain() ? compressionChain.getItem(0) : ItemStack.EMPTY;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (needsStackUpdate) {
            var determiningItem = storedItem != null ? storedItem : filterItem;
            compressedStacks = compressionChain.initStacks(unitCount, compressionCutoff, determiningItem);
            needsStackUpdate = false;
        }

        if (storedItem != null) {
            if (compressionEnabled) {
                compressedStacks.forEach(out::add);
            } else {
                out.add(storedItem, CompressionChain.clamp(unitCount.divide(unitFactor), CompressionChain.STACK_LIMIT));

                if (isFilterMismatched()) {
                    compressedStacks.keySet().stream()
                            .takeWhile(i -> !storedItem.equals(i))
                            .forEach(i -> out.add(i, compressedStacks.get(i)));
                }
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

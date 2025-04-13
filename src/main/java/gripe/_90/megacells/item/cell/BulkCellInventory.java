package gripe._90.megacells.item.cell;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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

public class BulkCellInventory implements StorageCell {
    private final ISaveProvider container;
    private final ItemStack stack;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final boolean compressionEnabled;
    private final CompressionChain compressionChain;
    private BigInteger unitCount;
    private final BigInteger unitFactor;
    private final int compressionCutoff;

    private final Map<Item, Long> availableStacks;
    private List<IPatternDetails> decompressionPatterns;

    private boolean isPersisted = true;

    BulkCellInventory(ItemStack stack, ISaveProvider container) {
        this.stack = stack;
        this.container = container;

        var cell = (BulkCellItem) stack.getItem();
        filterItem = (AEItemKey) cell.getConfigInventory(stack).getKey(0);

        storedItem = (AEItemKey) stack.get(MEGAComponents.BULK_CELL_ITEM);
        unitCount = stack.getOrDefault(MEGAComponents.BULK_CELL_UNIT_COUNT, BigInteger.ZERO);

        var determiningKey = storedItem != null ? storedItem : filterItem;
        var determiningItem = determiningKey != null ? determiningKey.getItem() : null;
        compressionChain = CompressionService.getChain(determiningItem);
        unitFactor = compressionChain.unitFactor(determiningItem);

        // Check newly-calculated factor against what's already recorded in order to adjust for a compression chain that
        // has changed from the left of the item filtered to or stored
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

        availableStacks = compressionChain.initStacks(unitCount, compressionCutoff, determiningItem);
        compressionEnabled = cell.getUpgrades(stack).isInstalled(MEGAItems.COMPRESSION_CARD);
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

    public AEItemKey getStoredItem() {
        return storedItem;
    }

    public long getStoredQuantity() {
        return CompressionChain.clamp(unitCount.divide(unitFactor), Long.MAX_VALUE);
    }

    public AEItemKey getFilterItem() {
        return filterItem;
    }

    private boolean isFilterMismatched() {
        return storedItem != null && !storedItem.equals(filterItem);
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public CompressionChain getCompressionChain() {
        return compressionChain;
    }

    public List<IPatternDetails> getDecompressionPatterns() {
        if (filterItem == null || !compressionEnabled || compressionChain.isEmpty() || isFilterMismatched()) {
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

        if (filterItem == null || isFilterMismatched()) {
            return 0;
        }

        if (!item.equals(filterItem) && (!compressionEnabled || !compressionChain.containsVariant(item.getItem()))) {
            return 0;
        }

        var factor = compressionChain.unitFactor(item.getItem());
        var units = BigInteger.valueOf(amount).multiply(factor);

        if (mode == Actionable.MODULATE) {
            if (storedItem == null) {
                storedItem = filterItem;
            }

            updateAvailableStacks(units);
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (storedItem == null || unitCount.signum() < 1 || !(what instanceof AEItemKey item)) {
            return 0;
        }

        var inRecovery = isFilterMismatched();

        if (!compressionChain.isEmpty() && !compressionChain.containsVariant(item.getItem())) {
            return 0;
        }

        if (!compressionEnabled && !item.equals(storedItem) && !inRecovery) {
            return 0;
        }

        if (inRecovery) {
            amount = Math.min(amount, getAvailableStacks().get(item));
        }

        var factor = compressionChain.unitFactor(item.getItem());
        var units = BigInteger.valueOf(amount).multiply(factor);
        var currentUnitCount = unitCount;

        if (currentUnitCount.compareTo(units) <= 0) {
            if (mode == Actionable.MODULATE) {
                storedItem = null;
                updateAvailableStacks(unitCount.negate());
            }

            return CompressionChain.clamp(currentUnitCount.divide(factor), Long.MAX_VALUE);
        } else {
            if (mode == Actionable.MODULATE) {
                updateAvailableStacks(units.negate());
            }

            return CompressionChain.clamp(units.divide(factor), Long.MAX_VALUE);
        }
    }

    private void updateAvailableStacks(BigInteger unitsToAdd) {
        unitCount = unitCount.add(unitsToAdd);
        saveChanges();
        compressionChain.updateStacks(availableStacks, unitsToAdd, compressionCutoff);
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
        if (!compressionEnabled && storedItem != null && !isFilterMismatched()) {
            out.add(storedItem, availableStacks.get(storedItem.getItem()));
        } else {
            availableStacks.forEach((item, amount) -> out.add(AEItemKey.of(item), amount));
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

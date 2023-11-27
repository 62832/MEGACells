package gripe._90.megacells.item.cell;

import static gripe._90.megacells.definition.MEGAItems.COMPRESSION_CARD;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.nbt.CompoundTag;
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

import gripe._90.megacells.misc.CompressionChain;
import gripe._90.megacells.misc.CompressionService;
import gripe._90.megacells.misc.DecompressionPattern;

public class BulkCellInventory implements StorageCell {
    private static final String KEY = "key";
    private static final String UNIT_COUNT = "smallestUnitCount";
    private static final String UNIT_FACTOR = "unitFactor";

    private static final long STACK_LIMIT = (long) Math.pow(2, 42);

    private final ISaveProvider container;
    private final ItemStack stack;

    private AEItemKey storedItem;
    private final AEItemKey filterItem;

    private final boolean compressionEnabled;
    private final CompressionChain compressionChain;
    private final Set<IPatternDetails> decompressionPatterns;
    private BigInteger unitCount;
    private final BigInteger unitFactor;

    private boolean isPersisted = true;

    BulkCellInventory(ItemStack stack, ISaveProvider container) {
        this.stack = stack;
        this.container = container;

        var cell = (BulkCellItem) stack.getItem();
        filterItem = (AEItemKey) cell.getConfigInventory(stack).getKey(0);

        storedItem = getTag().contains(KEY) ? AEItemKey.fromTag(getTag().getCompound(KEY)) : null;
        unitCount = !getTag().getString(UNIT_COUNT).isEmpty()
                ? new BigInteger(getTag().getString(UNIT_COUNT))
                : BigInteger.ZERO;

        compressionEnabled = cell.getUpgrades(stack).isInstalled(COMPRESSION_CARD);
        compressionChain = CompressionService.INSTANCE
                .getChain(storedItem != null ? storedItem : filterItem)
                .orElseGet(CompressionChain::new);
        decompressionPatterns = generateDecompressionPatterns();
        unitFactor = compressionChain.unitFactor(storedItem != null ? storedItem : filterItem);

        // Check newly-calculated factor against what's already recorded in order to adjust for a compression chain that
        // has changed from the left of the stored item
        var recordedFactor = !getTag().getString(UNIT_FACTOR).isEmpty()
                ? new BigInteger(getTag().getString(UNIT_FACTOR))
                : unitFactor;

        if (!unitFactor.equals(recordedFactor)) {
            unitCount = unitCount.multiply(unitFactor).divide(recordedFactor);
            saveChanges();
        }
    }

    private long clampedLong(BigInteger toClamp, long limit) {
        return toClamp.min(BigInteger.valueOf(limit)).longValue();
    }

    private CompoundTag getTag() {
        return stack.getOrCreateTag();
    }

    @Override
    public CellState getStatus() {
        if (unitCount.signum() < 1) {
            return CellState.EMPTY;
        }

        if (!Objects.equals(storedItem, filterItem)) {
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

    public Set<IPatternDetails> getDecompressionPatterns() {
        return decompressionPatterns;
    }

    private Set<IPatternDetails> generateDecompressionPatterns() {
        if (!compressionEnabled || compressionChain.isEmpty()) {
            return Set.of();
        }

        var patterns = new ObjectLinkedOpenHashSet<IPatternDetails>();
        var decompressionChain = compressionChain.limited().reversed();

        for (var variant : decompressionChain) {
            if (variant == decompressionChain.get(decompressionChain.size() - 1)) {
                continue;
            }

            var decompressed = decompressionChain.get(decompressionChain.indexOf(variant) + 1);
            patterns.add(new DecompressionPattern(decompressed.item(), variant, false));
        }

        var remainingChain = compressionChain.subList(decompressionChain.size() - 1, compressionChain.size());

        for (var variant : remainingChain) {
            if (variant == remainingChain.get(0)) {
                continue;
            }

            var decompressed = remainingChain.get(remainingChain.indexOf(variant) - 1);
            patterns.add(new DecompressionPattern(decompressed.item(), variant, true));
        }

        return patterns;
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

        if (filterItem == null || !Objects.equals(storedItem, filterItem)) {
            return 0;
        }

        if (!compressionEnabled && !what.equals(filterItem)) {
            return 0;
        }

        if (compressionEnabled && !what.equals(filterItem) && !compressionChain.containsVariant(item)) {
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

        if (filterItem == null || !Objects.equals(storedItem, filterItem)) {
            return 0;
        }

        if (!compressionEnabled && (unitCount.divide(unitFactor).signum() < 1 || !what.equals(storedItem))) {
            return 0;
        }

        if (compressionEnabled && !what.equals(filterItem) && !compressionChain.containsVariant(item)) {
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
            getTag().remove(UNIT_FACTOR);
        } else {
            getTag().put(KEY, storedItem.toTagGeneric());
            getTag().putString(UNIT_COUNT, unitCount.toString());
            getTag().putString(UNIT_FACTOR, unitFactor.toString());
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

                    if (count.divide(compressionFactor).signum() == 1 && variant != chain.get(chain.size() - 1)) {
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

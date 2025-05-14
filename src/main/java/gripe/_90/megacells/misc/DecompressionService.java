package gripe._90.megacells.misc;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.nbt.CompoundTag;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;

import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.part.DecompressionModulePart;

public class DecompressionService implements IGridService, IGridServiceProvider, ICraftingProvider {
    private static final String TAG_PATTERN_PRIORITY = "dcp";

    private final List<IChestOrDrive> cellHosts = new ObjectArrayList<>();
    private final List<IPatternDetails> patterns = new ObjectArrayList<>();

    private final IGrid grid;
    private int installedModules;

    private final Object2LongMap<AEKey> patternOutputs = new Object2LongOpenHashMap<>();
    private int patternPriority;
    private boolean priorityLocked;

    public DecompressionService(IGrid grid, ICraftingService craftingService) {
        this.grid = grid;
        craftingService.addGlobalCraftingProvider(this);
    }

    @Override
    public void addNode(IGridNode node, @Nullable CompoundTag savedData) {
        if (node.getOwner() instanceof IChestOrDrive cellHost) {
            cellHosts.add(cellHost);
        }

        if (node.getOwner() instanceof DecompressionModulePart) {
            installedModules++;

            if (!priorityLocked && savedData != null && savedData.contains(TAG_PATTERN_PRIORITY, CompoundTag.TAG_INT)) {
                patternPriority = savedData.getInt(TAG_PATTERN_PRIORITY);
                priorityLocked = true;
            }
        }
    }

    @Override
    public void saveNodeData(IGridNode node, CompoundTag savedData) {
        if (priorityLocked && node.getOwner() instanceof DecompressionModulePart) {
            savedData.putInt(TAG_PATTERN_PRIORITY, patternPriority);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof IChestOrDrive cellHost) {
            cellHosts.remove(cellHost);
        }

        if (node.getOwner() instanceof DecompressionModulePart) {
            installedModules--;
        }
    }

    @Override
    public void onServerStartTick() {
        if (!patternOutputs.isEmpty()) {
            for (var it = Object2LongMaps.fastIterator(patternOutputs); it.hasNext(); ) {
                var output = it.next();
                var what = output.getKey();
                var amount = output.getLongValue();
                var inserted = grid.getStorageService()
                        .getInventory()
                        .insert(what, amount, Actionable.MODULATE, IActionSource.empty());

                if (inserted >= amount) {
                    it.remove();
                } else if (inserted > 0) {
                    patternOutputs.put(what, amount - inserted);
                }
            }
        }
    }

    @Override
    public void onServerEndTick() {
        patterns.clear();

        if (installedModules > 0) {
            for (var cellHost : cellHosts) {
                for (int i = 0; i < cellHost.getCellCount(); i++) {
                    var cell = cellHost.getOriginalCellInventory(i);

                    if (cell instanceof BulkCellInventory bulkCell) {
                        patterns.addAll(bulkCell.getDecompressionPatterns());
                    }
                }
            }

            grid.getCraftingService().refreshGlobalCraftingProvider(this);
        }
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return installedModules > 0 ? patterns : List.of();
    }

    @Override
    public int getPatternPriority() {
        return patternPriority;
    }

    public void setPatternPriority(int priority, IGridNode node) {
        if (node.getOwner() instanceof DecompressionModulePart) {
            patternPriority = priority;
            priorityLocked = true;
            grid.getCraftingService().refreshGlobalCraftingProvider(this);
        }
    }

    @Override
    public boolean pushPattern(IPatternDetails details, KeyCounter[] inputHolder) {
        if (details instanceof DecompressionPattern) {
            var output = details.getPrimaryOutput();
            patternOutputs.merge(output.what(), output.amount(), Long::sum);
            return true;
        }

        return false;
    }

    @Override
    public boolean isBusy() {
        return false;
    }
}

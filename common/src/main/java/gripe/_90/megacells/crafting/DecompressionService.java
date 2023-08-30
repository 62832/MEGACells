package gripe._90.megacells.crafting;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.util.CompressionChain;

public class DecompressionService implements IGridService, IGridServiceProvider {
    private final Set<CompressionChain> decompressionChains = new ObjectLinkedOpenHashSet<>();
    private final List<IChestOrDrive> cellHosts = new ObjectArrayList<>();

    @Override
    public void addNode(IGridNode node) {
        if (node.getOwner() instanceof IChestOrDrive cellHost) {
            cellHosts.add(cellHost);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof IChestOrDrive cellHost) {
            cellHosts.remove(cellHost);
        }
    }

    @Override
    public void onServerStartTick() {
        decompressionChains.clear();

        for (var cellHost : cellHosts) {
            for (int i = 0; i < cellHost.getCellCount(); i++) {
                var cell = cellHost.getOriginalCellInventory(i);

                if (cell instanceof BulkCellInventory bulkCell && bulkCell.isCompressionEnabled()) {
                    decompressionChains.add(bulkCell.getDecompressionChain());
                }
            }
        }
    }

    public Set<CompressionChain> getDecompressionChains() {
        return Collections.unmodifiableSet(decompressionChains);
    }

    public Set<AEItemKey> getDecompressionPatterns(CompressionChain chain) {
        var patterns = new ObjectLinkedOpenHashSet<AEItemKey>();

        for (var variant : chain) {
            if (variant == chain.last()) {
                continue;
            }

            var pattern = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = chain.get(chain.indexOf(variant) + 1);

            DecompressionPatternEncoding.encode(
                    pattern.getOrCreateTag(), variant.item(), decompressed.item(), variant.factor());
            patterns.add(AEItemKey.of(pattern));
        }

        return Collections.unmodifiableSet(patterns);
    }
}

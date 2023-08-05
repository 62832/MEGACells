package gripe._90.megacells.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.crafting.DecompressionPatternEncoding;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;

public class DecompressionService implements IGridService, IGridServiceProvider {
    private final Set<Object2IntMap<AEItemKey>> decompressionChains = new ObjectLinkedOpenHashSet<>();
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

                if (!(cell instanceof BulkCellInventory bulkCell)) {
                    continue;
                }

                if (bulkCell.isCompressionEnabled()) {
                    addChain(bulkCell);
                }
            }
        }
    }

    private void addChain(BulkCellInventory cell) {
        var chain = CompressionService.INSTANCE
                .getChain(cell.getStoredItem())
                .map(c -> {
                    var keys = new ObjectArrayList<>(c.keySet());
                    Collections.reverse(keys);

                    var decompressed = new Object2IntLinkedOpenHashMap<AEItemKey>();
                    var highest = keys.indexOf(cell.getHighestCompressed());

                    if (highest > -1) {
                        for (var key : keys.subList(highest, keys.size())) {
                            decompressed.put(key, c.getInt(key));
                        }
                    }

                    return decompressed;
                })
                .orElseGet(Object2IntLinkedOpenHashMap::new);

        if (!chain.isEmpty()) {
            decompressionChains.add(chain);
        }
    }

    public Set<Object2IntMap<AEItemKey>> getDecompressionChains() {
        return decompressionChains;
    }

    public Set<AEItemKey> getDecompressionPatterns(Object2IntMap<AEItemKey> compressionChain) {
        var variants = new ObjectArrayList<>(compressionChain.keySet());
        var patterns = new ObjectLinkedOpenHashSet<AEItemKey>();

        for (var variant : variants) {
            if (variant == variants.get(variants.size() - 1)) {
                continue;
            }

            var pattern = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = variants.get(variants.indexOf(variant) + 1);
            var factor = compressionChain.getInt(decompressed);

            DecompressionPatternEncoding.encode(pattern.getOrCreateTag(), variant, decompressed, factor);
            patterns.add(AEItemKey.of(pattern));
        }

        return patterns;
    }
}

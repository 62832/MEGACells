package gripe._90.megacells.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.world.item.ItemStack;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.AEItemKey;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;

import gripe._90.megacells.crafting.DecompressionPatternEncoding;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.MEGABulkCell;

public class DecompressionService implements IGridService, IGridServiceProvider {
    private final Set<Object2IntMap<AEItemKey>> decompressionChains = new ObjectLinkedOpenHashSet<>();
    private final List<ChestBlockEntity> chests = new ObjectArrayList<>();
    private final List<DriveBlockEntity> drives = new ObjectArrayList<>();

    @Override
    public void addNode(IGridNode node) {
        if (node.getOwner()instanceof ChestBlockEntity chest) {
            chests.add(chest);
        }

        if (node.getOwner()instanceof DriveBlockEntity drive) {
            drives.add(drive);
        }
    }

    @Override
    public void onServerStartTick() {
        for (var chest : chests) {
            addChain(chest.getCell());
        }

        for (var drive : drives) {
            for (var cell : drive.getInternalInventory()) {
                addChain(cell);
            }
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner()instanceof ChestBlockEntity chest) {
            removeChain(chest.getCell());
            chests.remove(chest);
        }

        if (node.getOwner()instanceof DriveBlockEntity drive) {
            for (var cell : drive.getInternalInventory()) {
                removeChain(cell);
            }

            drives.remove(drive);
        }
    }

    private Object2IntMap<AEItemKey> getChain(ItemStack cell) {
        var cellInv = MEGABulkCell.HANDLER.getCellInventory(cell, null);

        if (cellInv != null && cellInv.compressionEnabled) {
            return CompressionService.INSTANCE.getChain(cellInv.getStoredItem()).map(c -> {
                var keys = new ObjectArrayList<>(c.keySet());
                Collections.reverse(keys);

                var decompressed = new Object2IntLinkedOpenHashMap<AEItemKey>();
                keys.forEach(k -> decompressed.put(k, c.getInt(k)));
                return decompressed;
            }).orElseGet(Object2IntLinkedOpenHashMap::new);
        }

        return new Object2IntLinkedOpenHashMap<>();
    }

    private void addChain(ItemStack cell) {
        var chain = getChain(cell);

        if (!chain.isEmpty()) {
            decompressionChains.add(chain);
        }
    }

    private void removeChain(ItemStack cell) {
        decompressionChains.remove(getChain(cell));
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

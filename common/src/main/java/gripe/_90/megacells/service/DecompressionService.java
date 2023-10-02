package gripe._90.megacells.service;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.world.item.ItemStack;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.api.storage.cells.StorageCell;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.me.storage.DelegatingMEInventory;
import appeng.me.storage.DriveWatcher;

import gripe._90.megacells.crafting.DecompressionPatternEncoding;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

public class DecompressionService implements IGridService, IGridServiceProvider {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Class<?> CHEST_MONITOR_CLASS;
    private static final VarHandle CHEST_MONITOR_HANDLE;
    private static final VarHandle CHEST_CELL_HANDLE;
    private static final MethodHandle DRIVE_DELEGATE_HANDLE;
    private static final VarHandle DRIVE_WATCHERS_HANDLE;

    static {
        try {
            CHEST_MONITOR_CLASS = Class.forName("appeng.blockentity.storage.ChestBlockEntity$ChestMonitorHandler");
            CHEST_MONITOR_HANDLE = MethodHandles.privateLookupIn(ChestBlockEntity.class, LOOKUP)
                    .findVarHandle(ChestBlockEntity.class, "cellHandler", CHEST_MONITOR_CLASS);
            CHEST_CELL_HANDLE = MethodHandles.privateLookupIn(CHEST_MONITOR_CLASS, LOOKUP)
                    .findVarHandle(CHEST_MONITOR_CLASS, "cellInventory", StorageCell.class);

            DRIVE_WATCHERS_HANDLE = MethodHandles.privateLookupIn(DriveBlockEntity.class, LOOKUP)
                    .findVarHandle(DriveBlockEntity.class, "invBySlot", DriveWatcher[].class);
            DRIVE_DELEGATE_HANDLE = MethodHandles.privateLookupIn(DelegatingMEInventory.class, LOOKUP)
                    .findVirtual(DelegatingMEInventory.class, "getDelegate", MethodType.methodType(MEStorage.class));
        } catch (NoSuchFieldException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create DecompressionService method handles", e);
        }
    }

    private final Set<Object2IntMap<AEItemKey>> decompressionChains = new ObjectLinkedOpenHashSet<>();
    private final List<ChestBlockEntity> chests = new ObjectArrayList<>();
    private final List<DriveBlockEntity> drives = new ObjectArrayList<>();

    @Override
    public void addNode(IGridNode node) {
        if (node.getOwner() instanceof ChestBlockEntity chest) {
            chests.add(chest);
        }

        if (node.getOwner() instanceof DriveBlockEntity drive) {
            drives.add(drive);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof ChestBlockEntity chest) {
            chests.remove(chest);
        }

        if (node.getOwner() instanceof DriveBlockEntity drive) {
            drives.remove(drive);
        }
    }

    @Override
    public void onServerStartTick() {
        decompressionChains.clear();

        try {
            for (var chest : chests) {
                addChain(getCellByChest(chest));
            }

            for (var drive : drives) {
                for (int i = 0; i < drive.getCellCount(); i++) {
                    addChain(getCellByDriveSlot(drive, i));
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to invoke DecompressionService method handles", e);
        }
    }

    private StorageCell getCellByChest(ChestBlockEntity chest) {
        var monitor = CHEST_MONITOR_HANDLE.get(chest);
        return monitor != null ? (StorageCell) CHEST_CELL_HANDLE.get(monitor) : null;
    }

    private StorageCell getCellByDriveSlot(DriveBlockEntity drive, int slot) throws Throwable {
        var watchers = (DriveWatcher[]) DRIVE_WATCHERS_HANDLE.get(drive);
        return watchers[slot] != null ? (StorageCell) DRIVE_DELEGATE_HANDLE.invoke(watchers[slot]) : null;
    }

    private Object2IntMap<AEItemKey> getChain(BulkCellInventory cell) {
        if (cell.compressionEnabled) {
            return CompressionService.INSTANCE
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
        }

        return new Object2IntLinkedOpenHashMap<>();
    }

    private void addChain(StorageCell cell) {
        if (!(cell instanceof BulkCellInventory bulkCell)) {
            return;
        }

        var chain = getChain(bulkCell);

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

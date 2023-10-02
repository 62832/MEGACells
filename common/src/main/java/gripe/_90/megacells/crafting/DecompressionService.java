package gripe._90.megacells.crafting;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.world.item.ItemStack;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.cells.StorageCell;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.me.storage.DelegatingMEInventory;
import appeng.me.storage.DriveWatcher;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.part.DecompressionModulePart;

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

    private final List<ChestBlockEntity> chests = new ObjectArrayList<>();
    private final List<DriveBlockEntity> drives = new ObjectArrayList<>();
    private final List<IPatternDetails> patterns = new ObjectArrayList<>();
    private final List<DecompressionModulePart> modules = new ObjectArrayList<>();

    @Override
    public void addNode(IGridNode node) {
        if (node.getOwner() instanceof ChestBlockEntity chest) {
            chests.add(chest);
        }

        if (node.getOwner() instanceof DriveBlockEntity drive) {
            drives.add(drive);
        }

        if (node.getOwner() instanceof DecompressionModulePart module) {
            modules.add(module);
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

        if (node.getOwner() instanceof DecompressionModulePart module) {
            modules.remove(module);
        }
    }

    @Override
    public void onServerStartTick() {
        patterns.clear();

        for (var chest : chests) {
            var cell = getCellByChest(chest);

            if (cell instanceof BulkCellInventory bulkCell && bulkCell.isCompressionEnabled()) {
                patterns.addAll(generatePatterns(bulkCell));
            }
        }

        for (var drive : drives) {
            for (var i = 0; i < drive.getCellCount(); i++) {
                try {
                    var cell = getCellByDriveSlot(drive, i);

                    if (cell instanceof BulkCellInventory bulkCell && bulkCell.isCompressionEnabled()) {
                        patterns.addAll(generatePatterns(bulkCell));
                    }
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (var module : modules) {
            ICraftingProvider.requestUpdate(module.getMainNode());
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

    public List<IPatternDetails> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }

    private Set<IPatternDetails> generatePatterns(BulkCellInventory cell) {
        var fullChain = cell.getCompressionChain();

        if (fullChain.isEmpty()) {
            return Set.of();
        }

        var patterns = new ObjectLinkedOpenHashSet<IPatternDetails>();
        var decompressionChain = fullChain.reversed();

        for (var variant : decompressionChain) {
            if (variant == decompressionChain.last()) {
                continue;
            }

            var pattern = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = decompressionChain.get(decompressionChain.indexOf(variant) + 1);

            DecompressionPatternEncoding.encode(pattern.getOrCreateTag(), decompressed.item(), variant, false);
            patterns.add(new DecompressionPattern(pattern));
        }

        var compressionChain = fullChain.subList(decompressionChain.size() - 1, fullChain.size());

        for (var variant : compressionChain) {
            if (variant == compressionChain.get(0)) {
                continue;
            }

            var pattern = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = compressionChain.get(compressionChain.indexOf(variant) - 1);

            DecompressionPatternEncoding.encode(pattern.getOrCreateTag(), decompressed.item(), variant, true);
            patterns.add(new DecompressionPattern(pattern));
        }

        return patterns;
    }
}

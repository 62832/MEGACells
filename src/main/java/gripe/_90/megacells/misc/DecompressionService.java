package gripe._90.megacells.misc;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.nbt.CompoundTag;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.crafting.ICraftingProvider;

import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.part.DecompressionModulePart;

public class DecompressionService implements IGridService, IGridServiceProvider {
    private final List<IChestOrDrive> cellHosts = new ObjectArrayList<>();
    private final List<IPatternDetails> patterns = new ObjectArrayList<>();
    private final List<DecompressionModulePart> modules = new ObjectArrayList<>();

    @Override
    public void addNode(IGridNode node, CompoundTag savedData) {
        if (node.getOwner() instanceof IChestOrDrive cellHost) {
            cellHosts.add(cellHost);
        }

        if (node.getOwner() instanceof DecompressionModulePart module) {
            modules.add(module);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof IChestOrDrive cellHost) {
            cellHosts.remove(cellHost);
        }

        if (node.getOwner() instanceof DecompressionModulePart module) {
            modules.remove(module);
        }
    }

    @Override
    public void onServerStartTick() {
        patterns.clear();

        for (var cellHost : cellHosts) {
            for (int i = 0; i < cellHost.getCellCount(); i++) {
                var cell = cellHost.getOriginalCellInventory(i);

                if (cell instanceof BulkCellInventory bulkCell) {
                    patterns.addAll(bulkCell.getDecompressionPatterns());
                }
            }
        }

        for (var module : modules) {
            ICraftingProvider.requestUpdate(module.getMainNode());
        }
    }

    public List<IPatternDetails> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }
}

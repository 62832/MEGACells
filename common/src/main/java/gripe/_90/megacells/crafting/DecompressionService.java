package gripe._90.megacells.crafting;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.world.item.ItemStack;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.part.DecompressionModulePart;

public class DecompressionService implements IGridService, IGridServiceProvider {
    private final List<IChestOrDrive> cellHosts = new ObjectArrayList<>();
    private final List<IPatternDetails> patterns = new ObjectArrayList<>();
    private final List<DecompressionModulePart> modules = new ObjectArrayList<>();

    @Override
    public void addNode(IGridNode node) {
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

                if (cell instanceof BulkCellInventory bulkCell && bulkCell.isCompressionEnabled()) {
                    patterns.addAll(generatePatterns(bulkCell));
                }
            }
        }

        if (!modules.isEmpty()) {
            ICraftingProvider.requestUpdate(modules.get(0).getMainNode());
        }
    }

    public List<IPatternDetails> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }

    private Set<IPatternDetails> generatePatterns(BulkCellInventory cell) {
        var patterns = new ObjectLinkedOpenHashSet<IPatternDetails>();
        var chain = cell.getDecompressionChain();
        var count = 1;

        for (var variant : chain) {
            if (variant == chain.last()) {
                continue;
            }

            var patternItem = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = chain.get(chain.indexOf(variant) + 1);

            DecompressionPatternEncoding.encode(
                    patternItem.getOrCreateTag(), variant.item(), decompressed.item(), count, variant.factor());
            var pattern = new DecompressionPattern(AEItemKey.of(patternItem.getItem(), patternItem.getTag()));
            patterns.add(pattern);
            count *= variant.factor();
        }

        return patterns;
    }
}

package gripe._90.megacells.compression;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.nbt.CompoundTag;
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

                if (cell instanceof BulkCellInventory bulkCell && bulkCell.isCompressionEnabled()) {
                    patterns.addAll(generatePatterns(bulkCell));
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

    private Set<IPatternDetails> generatePatterns(BulkCellInventory cell) {
        var fullChain = cell.getCompressionChain();

        if (fullChain.isEmpty()) {
            return Set.of();
        }

        var patterns = new ObjectLinkedOpenHashSet<IPatternDetails>();
        var decompressionChain = fullChain.limited().reversed();

        for (var variant : decompressionChain) {
            if (variant == decompressionChain.last()) {
                continue;
            }

            var pattern = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = decompressionChain.get(decompressionChain.indexOf(variant) + 1);

            encodePattern(pattern.getOrCreateTag(), decompressed.item(), variant, false);
            patterns.add(new DecompressionPattern(pattern));
        }

        var compressionChain = fullChain.subList(decompressionChain.size() - 1, fullChain.size());

        for (var variant : compressionChain) {
            if (variant == compressionChain.get(0)) {
                continue;
            }

            var pattern = new ItemStack(MEGAItems.DECOMPRESSION_PATTERN);
            var decompressed = compressionChain.get(compressionChain.indexOf(variant) - 1);

            encodePattern(pattern.getOrCreateTag(), decompressed.item(), variant, true);
            patterns.add(new DecompressionPattern(pattern));
        }

        return patterns;
    }

    private void encodePattern(CompoundTag tag, AEItemKey base, CompressionVariant variant, boolean toCompress) {
        tag.put(DecompressionPattern.NBT_VARIANT, variant.item().toTag());
        tag.put(DecompressionPattern.NBT_BASE, base.toTag());
        tag.putInt(DecompressionPattern.NBT_FACTOR, variant.factor());
        tag.putBoolean(DecompressionPattern.NBT_TO_COMPRESS, toCompress);
    }
}

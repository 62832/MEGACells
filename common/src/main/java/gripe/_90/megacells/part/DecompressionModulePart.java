package gripe._90.megacells.part;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.items.parts.PartModels;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;

import gripe._90.megacells.crafting.DecompressionPatternDecoder;
import gripe._90.megacells.crafting.MEGADecompressionPattern;
import gripe._90.megacells.service.DecompressionService;
import gripe._90.megacells.util.Utils;

public class DecompressionModulePart extends AEBasePart implements ICraftingProvider, IGridTickable {
    @PartModels
    public static final IPartModel MODEL = new PartModel(Utils.makeId("part/decompression_module"));

    private final List<IPatternDetails> patterns = new ObjectArrayList<>();
    private final Object2LongMap<AEKey> outputs = new Object2LongOpenHashMap<>();

    public DecompressionModulePart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridTickable.class, this)
                .addService(ICraftingProvider.class, this)
                .setIdlePowerUsage(10.0);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return patterns;
    }

    @Override
    public int getPatternPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!getMainNode().isActive() || !(patternDetails instanceof MEGADecompressionPattern pattern)) {
            return false;
        }

        var output = pattern.getPrimaryOutput();
        outputs.mergeLong(output.what(), output.amount(), Long::sum);
        return true;
    }

    @Override
    public boolean isBusy() {
        return !outputs.isEmpty();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(3, 3, 12, 13, 13, 16);
        bch.addBox(5, 5, 11, 11, 11, 12);
    }

    @Override
    public IPartModel getStaticModels() {
        return MODEL;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        patterns.clear();
        var grid = getMainNode().getGrid();

        if (grid != null) {
            var decompressionService = grid.getService(DecompressionService.class);

            for (var chain : decompressionService.getDecompressionChains()) {
                var patternItems = decompressionService.getDecompressionPatterns(chain);
                var decodedPatterns = patternItems.stream()
                        .map(p -> DecompressionPatternDecoder.INSTANCE.decodePattern(p, getLevel()));
                patterns.addAll(decodedPatterns.toList());
            }

            var storage = grid.getStorageService();

            for (var output : outputs.object2LongEntrySet()) {
                var what = output.getKey();
                var amount = output.getLongValue();
                var inserted =
                        storage.getInventory().insert(what, amount, Actionable.MODULATE, IActionSource.ofMachine(this));

                if (inserted >= amount) {
                    outputs.removeLong(what);
                } else if (inserted > 0) {
                    outputs.put(what, amount - inserted);
                }
            }

            ICraftingProvider.requestUpdate(getMainNode());
        }

        return TickRateModulation.URGENT;
    }
}

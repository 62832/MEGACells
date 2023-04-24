package gripe._90.megacells.part;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.resources.ResourceLocation;

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
import appeng.core.AppEng;
import appeng.core.settings.TickRates;
import appeng.items.parts.PartModels;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;

import gripe._90.megacells.crafting.DecompressionPatternDecoder;
import gripe._90.megacells.crafting.MEGADecompressionPattern;
import gripe._90.megacells.service.DecompressionService;

public class DecompressionModulePart extends AEBasePart implements ICraftingProvider {
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(AppEng.MOD_ID, "part/export_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));

    private final Object2LongMap<AEKey> outputs = new Object2LongOpenHashMap<>();

    public DecompressionModulePart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridTickable.class, new OutputInjector())
                .addService(IGridTickable.class, new PatternUpdater())
                .addService(ICraftingProvider.class, this);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        var patterns = new ObjectArrayList<IPatternDetails>();
        var grid = getMainNode().getGrid();

        if (grid != null) {
            var decompressionService = grid.getService(DecompressionService.class);

            for (var chain : decompressionService.getDecompressionChains()) {
                var patternItems = decompressionService.getDecompressionPatterns(chain);
                var decodedPatterns = patternItems.stream()
                        .map(p -> DecompressionPatternDecoder.INSTANCE.decodePattern(p, getLevel()));
                patterns.addAll(decodedPatterns.toList());
            }
        }

        return patterns;
    }

    @Override
    public int getPatternPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!getMainNode().isActive() || !(patternDetails instanceof MEGADecompressionPattern)) {
            return false;
        }

        var output = patternDetails.getPrimaryOutput();
        outputs.mergeLong(output.what(), 1L, Long::sum);
        return true;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    private boolean injectOutputs() {
        var didWork = false;
        var grid = getMainNode().getGrid();

        if (grid != null) {
            var storage = grid.getStorageService().getInventory();

            for (var stack : outputs.object2LongEntrySet()) {
                var sizeBefore = stack.getLongValue();
                var inserted = storage.insert(stack.getKey(), stack.getLongValue(), Actionable.MODULATE,
                        IActionSource.ofMachine(getMainNode()::getNode));

                if (inserted >= stack.getLongValue()) {
                    outputs.removeLong(stack.getKey());
                } else {
                    stack.setValue(stack.getLongValue() - inserted);
                }

                inserted = Math.max(0, sizeBefore - stack.getLongValue());

                if (inserted > 0) {
                    didWork = true;
                }
            }
        }

        return didWork;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    private class OutputInjector implements IGridTickable {
        @Override
        public TickingRequest getTickingRequest(IGridNode node) {
            return new TickingRequest(TickRates.Interface, outputs.isEmpty(), true);
        }

        @Override
        public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
            if (!getMainNode().isActive()) {
                return TickRateModulation.SLEEP;
            }

            return !outputs.isEmpty() ? injectOutputs()
                    ? TickRateModulation.URGENT
                    : TickRateModulation.SLOWER
                    : TickRateModulation.SLEEP;
        }
    }
    
    private class PatternUpdater implements IGridTickable {
        @Override
        public TickingRequest getTickingRequest(IGridNode node) {
            return new TickingRequest(1, 1, false, false);
        }

        @Override
        public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
            ICraftingProvider.requestUpdate(getMainNode());
            return TickRateModulation.URGENT;
        }
    }
}

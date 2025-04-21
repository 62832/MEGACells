package gripe._90.megacells.item.part;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

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
import appeng.helpers.IPriorityHost;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.PriorityMenu;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.misc.DecompressionPattern;
import gripe._90.megacells.misc.DecompressionService;

public class DecompressionModulePart extends AEBasePart implements ICraftingProvider, IPriorityHost, IGridTickable {
    @PartModels
    private static final IPartModel MODEL = new PartModel(MEGACells.makeId("part/decompression_module"));

    private final Object2LongMap<AEKey> outputs = new Object2LongOpenHashMap<>();

    private int priority = 0;

    public DecompressionModulePart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridTickable.class, this)
                .addService(ICraftingProvider.class, this)
                .setIdlePowerUsage(10.0);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        data.putInt("priority", priority);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        priority = data.getInt("priority");
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getService(DecompressionService.class).getPatterns() : List.of();
    }

    @Override
    public int getPatternPriority() {
        return priority;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!getMainNode().isActive() || !(patternDetails instanceof DecompressionPattern pattern)) {
            return false;
        }

        var output = pattern.getPrimaryOutput();
        outputs.merge(output.what(), output.amount(), Long::sum);

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
        return true;
    }

    @Override
    public boolean isBusy() {
        return false;
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
        return new TickingRequest(1, 1, outputs.isEmpty());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var storage = node.getGrid().getStorageService().getInventory();

        for (var it = Object2LongMaps.fastIterator(outputs); it.hasNext(); ) {
            var output = it.next();
            var what = output.getKey();
            var amount = output.getLongValue();
            var inserted = storage.insert(what, amount, Actionable.MODULATE, IActionSource.ofMachine(this));

            if (inserted >= amount) {
                it.remove();
            } else if (inserted > 0) {
                outputs.put(what, amount - inserted);
            }
        }

        return TickRateModulation.URGENT;
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            MenuOpener.open(PriorityMenu.TYPE, player, MenuLocators.forPart(this));
        }

        return true;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int newPriority) {
        priority = newPriority;
        getHost().markForSave();
        ICraftingProvider.requestUpdate(getMainNode());
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.DECOMPRESSION_MODULE.stack();
    }
}

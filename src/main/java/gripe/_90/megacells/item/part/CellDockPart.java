package gripe._90.megacells.item.part;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.blockentity.inventory.AppEngCellInventory;
import appeng.helpers.IPriorityHost;
import appeng.me.storage.DriveWatcher;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.util.InteractionUtil;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;

public class CellDockPart extends AEBasePart
        implements InternalInventoryHost, IChestOrDrive, IStorageProvider, IPriorityHost {
    private static final Logger LOGGER = LoggerFactory.getLogger(CellDockPart.class);

    private final AppEngCellInventory cellInventory = new AppEngCellInventory(this, 1);
    private DriveWatcher cellWatcher;
    private boolean isCached = false;
    private boolean wasOnline = false;
    private int priority = 0;

    private Item clientCell;
    private CellState clientCellState = CellState.ABSENT;
    private byte spin;

    public CellDockPart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode()
                .setIdlePowerUsage(0.5)
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IStorageProvider.class, this);
        cellInventory.setFilter(new Filter());
    }

    @Override
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        cellInventory.setItemDirect(
                0, input.read("cell", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        priority = input.getIntOr("priority", 0);
        spin = input.getByteOr("spin", (byte) 0);
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        output.store("cell", ItemStack.OPTIONAL_CODEC, getCell());
        output.putInt("priority", priority);
        output.putByte("spin", spin);
    }

    @Override
    public boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        var oldCell = clientCell;
        var oldCellState = clientCellState;
        var oldSpin = spin;

        clientCell = Item.byId(data.readVarInt());
        clientCellState = data.readEnum(CellState.class);
        spin = data.readByte();

        return changed || oldCell != clientCell || oldCellState != clientCellState || oldSpin != spin;
    }

    @Override
    public void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeVarInt(Item.getId(getCell().getItem()));
        data.writeEnum(getCellStatus(0));
        data.writeByte(spin);
    }

    @Override
    public void readVisualStateFromNBT(ValueInput input) {
        super.readVisualStateFromNBT(input);

        try {
            clientCell = BuiltInRegistries.ITEM.getValue(Identifier.parse(input.getStringOr("cellId", "")));
        } catch (Exception e) {
            LOGGER.warn("Couldn't read cell item for {} from {}", this, input);
            clientCell = null;
        }

        try {
            clientCellState = CellState.valueOf(input.getStringOr("cellStatus", ""));
        } catch (Exception e) {
            LOGGER.warn("Couldn't read cell status for {} from {}", this, input);
            clientCellState = CellState.ABSENT;
        }

        spin = input.getByteOr("spin", (byte) 0);
    }

    @Override
    public void writeVisualStateToNBT(ValueOutput output) {
        super.writeVisualStateToNBT(output);
        output.putString(
                "cellId", BuiltInRegistries.ITEM.getKey(getCell().getItem()).toString());
        output.putString("cellStatus", getCellStatus(0).name());
        output.putByte("spin", spin);
    }

    private void recalculateDisplay() {
        getHost().markForUpdate();
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        var online = getMainNode().isOnline();

        if (online != wasOnline) {
            wasOnline = online;
            IStorageProvider.requestUpdate(getMainNode());
            recalculateDisplay();
        }
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!isClientSide()) {
            MenuOpener.open(MEGAMenus.CELL_DOCK.get(), player, MenuLocators.forPart(this));
        }

        return true;
    }

    @Override
    public boolean onUseItemOn(ItemStack heldItem, Player player, InteractionHand hand, Vec3 pos) {
        if (InteractionUtil.canWrenchRotate(heldItem)) {
            if (!isClientSide()) {
                spin = (byte) ((spin + 1) % 4);
                getHost().markForSave();
                getHost().markForUpdate();
            }

            return true;
        } else {
            return super.onUseItemOn(heldItem, player, hand, pos);
        }
    }

    @Override
    public void onPlacement(Player player) {
        super.onPlacement(player);
        var rotation = (byte) (Mth.floor(player.getYRot() * 4F / 360F + 2.5D) & 3);

        if (getSide() == Direction.UP || getSide() == Direction.DOWN) {
            spin = rotation;
        }
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(MEGAMenus.CELL_DOCK.get(), player, MenuLocators.forPart(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.CELL_DOCK.stack();
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        drops.add(getCell());
    }

    public AppEngCellInventory getCellInventory() {
        return cellInventory;
    }

    private ItemStack getCell() {
        return cellInventory.getStackInSlot(0);
    }

    @Override
    public int getCellCount() {
        return 1;
    }

    @Override
    public boolean isCellBlinking(int slot) {
        return false;
    }

    @Nullable
    @Override
    public Item getCellItem(int slot) {
        if (slot != 0) {
            return null;
        }
        return isClientSide() ? clientCell : cellInventory.getStackInSlot(slot).getItem();
    }

    @Nullable
    @Override
    public MEStorage getCellInventory(int slot) {
        return slot == 0 && cellWatcher != null ? cellWatcher : null;
    }

    @Nullable
    @Override
    public StorageCell getOriginalCellInventory(int slot) {
        return slot == 0 && cellWatcher != null ? cellWatcher.getCell() : null;
    }

    @Override
    public CellState getCellStatus(int slot) {
        return isClientSide()
                ? clientCellState
                : slot == 0 && cellWatcher != null ? cellWatcher.getStatus() : CellState.ABSENT;
    }

    public byte getSpin() {
        return spin;
    }

    @Override
    public void mountInventories(IStorageMounts storageMounts) {
        if (getMainNode().isOnline()) {
            updateState();

            if (cellWatcher != null) {
                storageMounts.mount(cellWatcher, priority);
            }
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        getHost().markForSave();
        getHost().markForUpdate();
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (isCached) {
            isCached = false;
            updateState();
        }

        IStorageProvider.requestUpdate(getMainNode());
    }

    private void updateState() {
        if (!isCached) {
            cellWatcher = null;
            cellInventory.setHandler(0, null);
            var power = 0.5;

            if (!getCell().isEmpty()) {
                var cell = StorageCells.getCellInventory(getCell(), this::onCellContentChanged);

                if (cell != null) {
                    cellWatcher = new DriveWatcher(cell, this::recalculateDisplay);
                    cellInventory.setHandler(0, cell);
                    power += cell.getIdleDrain();
                }
            }

            getMainNode().setIdlePowerUsage(power);
            isCached = true;
        }
    }

    private void onCellContentChanged() {
        getLevel().blockEntityChanged(getBlockEntity().getBlockPos());
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int newValue) {
        priority = newValue;
        getHost().markForSave();

        isCached = false;
        updateState();

        IStorageProvider.requestUpdate(getMainNode());
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(3, 3, 12, 13, 13, 16);
        bch.addBox(5, 5, 11, 11, 11, 12);
    }

    private static class Filter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return StorageCells.isCellHandled(stack);
        }
    }
}

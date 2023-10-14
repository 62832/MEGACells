package gripe._90.megacells.item.part;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.blockentity.inventory.AppEngCellInventory;
import appeng.helpers.IPriorityHost;
import appeng.items.parts.PartModels;
import appeng.me.storage.DriveWatcher;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.menu.CellDockMenu;

public class CellDockPart extends AEBasePart
        implements InternalInventoryHost, IChestOrDrive, IPriorityHost, IStorageProvider {
    // TODO
    @PartModels
    public static final IPartModel MODEL = new PartModel(MEGACells.makeId("part/decompression_module"));

    private final AppEngCellInventory cellInventory = new AppEngCellInventory(this, 1);
    private DriveWatcher cellWatcher;
    private boolean isCached = false;
    private int priority = 0;

    public CellDockPart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).addService(IStorageProvider.class, this);
        cellInventory.setFilter(new CellInventoryFilter());
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        cellInventory.setItemDirect(0, ItemStack.of(data.getCompound("cell")));
        priority = data.getInt("priority");
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);

        var cell = new CompoundTag();
        getCell().save(cell);
        data.put("cell", cell);

        data.putInt("priority", priority);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            MenuOpener.open(CellDockMenu.TYPE, player, MenuLocators.forPart(this));
        }

        return true;
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
        return slot == 0 ? cellInventory.getStackInSlot(slot).getItem() : null;
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
        return slot == 0 && cellWatcher != null ? cellWatcher.getStatus() : CellState.ABSENT;
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
    public void saveChanges() {
        getHost().markForSave();
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        if (isCached) {
            isCached = false;
            updateState();
        }

        IStorageProvider.requestUpdate(getMainNode());
        getHost().markForSave();
    }

    private void updateState() {
        if (!isCached) {
            cellWatcher = null;
            cellInventory.setHandler(0, null);

            if (!getCell().isEmpty()) {
                isCached = true;
                var cell = StorageCells.getCellInventory(getCell(), this::onCellContentChanged);

                if (cell != null) {
                    cellWatcher = new DriveWatcher(cell, () -> {});
                    cellInventory.setHandler(0, cell);

                    getMainNode().setIdlePowerUsage(0.5 + cell.getIdleDrain());
                }
            }
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
        isCached = false;
        saveChanges();
        updateState();

        IStorageProvider.requestUpdate(getMainNode());
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(CellDockMenu.TYPE, player, MenuLocators.forPart(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.CELL_DOCK.stack();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        // TODO
        bch.addBox(3, 3, 12, 13, 13, 16);
        bch.addBox(5, 5, 11, 11, 11, 12);
    }

    @Override
    public IPartModel getStaticModels() {
        return MODEL;
    }

    @Override
    public void renderDynamic(
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int combinedLightIn,
            int combinedOverlayIn) {
        // TODO
    }

    private static class CellInventoryFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return StorageCells.isCellHandled(stack);
        }
    }
}

package gripe._90.megacells.item.part;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
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
import appeng.client.render.BakedModelUnwrapper;
import appeng.client.render.model.DriveBakedModel;
import appeng.client.render.tesr.CellLedRenderer;
import appeng.core.definitions.AEBlocks;
import appeng.helpers.IPriorityHost;
import appeng.items.parts.PartModels;
import appeng.me.storage.DriveWatcher;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.client.render.FaceRotatingModel;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;

public class CellDockPart extends AEBasePart
        implements InternalInventoryHost, IChestOrDrive, IStorageProvider, IPriorityHost {
    @PartModels
    private static final IPartModel MODEL = new PartModel(MEGACells.makeId("part/cell_dock"));

    private static final IAEItemFilter FILTER = new IAEItemFilter() {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return StorageCells.isCellHandled(stack);
        }
    };

    private final AppEngCellInventory cellInventory = new AppEngCellInventory(this, 1);
    private DriveWatcher cellWatcher;
    private boolean isCached = false;
    private boolean wasOnline = false;
    private int priority = 0;

    // Client-side cell attributes to display the proper dynamic model without synchronising the entire cell's inventory
    // when a dock comes into view
    private Item clientCell = Items.AIR;
    private CellState clientCellState = CellState.ABSENT;

    public CellDockPart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode()
                .setIdlePowerUsage(0.5)
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IStorageProvider.class, this);
        cellInventory.setFilter(FILTER);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        cellInventory.setItemDirect(0, ItemStack.parseOptional(registries, data.getCompound("cell")));
        priority = data.getInt("priority");
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        data.put("cell", getCell().saveOptional(registries));
        data.putInt("priority", priority);
    }

    @Override
    public boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);

        var oldCell = clientCell;
        var oldCellState = clientCellState;

        clientCell = BuiltInRegistries.ITEM.get(data.readResourceLocation());
        clientCellState = data.readEnum(CellState.class);

        return changed || oldCell != clientCell || oldCellState != clientCellState;
    }

    @Override
    public void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeResourceLocation(BuiltInRegistries.ITEM.getKey(getCell().getItem()));
        data.writeEnum(clientCellState = getCellStatus(0));
    }

    @Override
    public void readVisualStateFromNBT(CompoundTag data) {
        super.readVisualStateFromNBT(data);

        try {
            this.clientCell = BuiltInRegistries.ITEM.get(ResourceLocation.parse(data.getString("cellId")));
        } catch (Exception e) {
            MEGACells.LOGGER.warn("Couldn't read cell item for {} from {}", this, data);
            this.clientCell = Items.AIR;
        }

        try {
            this.clientCellState = CellState.valueOf(data.getString("cellStatus"));
        } catch (Exception e) {
            MEGACells.LOGGER.warn("Couldn't read cell status for {} from {}", this, data);
            this.clientCellState = CellState.ABSENT;
        }
    }

    @Override
    public void writeVisualStateToNBT(CompoundTag data) {
        super.writeVisualStateToNBT(data);
        data.putString(
                "cellId", BuiltInRegistries.ITEM.getKey(getCell().getItem()).toString());
        data.putString("cellStatus", getCellStatus(0).name());
    }

    private void recalculateDisplay() {
        var cellState = getCellStatus(0);

        if (clientCellState != cellState) {
            getHost().markForUpdate();
            clientCellState = cellState;
        }
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
        if (!player.getCommandSenderWorld().isClientSide()) {
            MenuOpener.open(MEGAMenus.CELL_DOCK, player, MenuLocators.forPart(this));
        }

        return true;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(MEGAMenus.CELL_DOCK, player, MenuLocators.forPart(this));
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
        return isClientSide()
                ? clientCellState
                : slot == 0 && cellWatcher != null ? cellWatcher.getStatus() : CellState.ABSENT;
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
    public IPartModel getStaticModels() {
        return MODEL;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(3, 3, 12, 13, 13, 16);
        bch.addBox(5, 5, 11, 11, 11, 12);
    }

    @Override
    public boolean requireDynamicRender() {
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void renderDynamic(
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int combinedLightIn,
            int combinedOverlayIn) {
        if (getLevel() == null || clientCell == Items.AIR) {
            return;
        }

        var driveModel = BakedModelUnwrapper.unwrap(
                Minecraft.getInstance()
                        .getModelManager()
                        .getBlockModelShaper()
                        .getBlockModel(AEBlocks.DRIVE.block().defaultBlockState()),
                DriveBakedModel.class);

        if (driveModel == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        var front = getSide() == Direction.UP || getSide() == Direction.DOWN ? Direction.NORTH : Direction.UP;
        var orientation = BlockOrientation.get(front, getSide());

        poseStack.mulPose(orientation.getQuaternion());
        poseStack.translate(-3F / 16, 5F / 16, -4F / 16);

        Minecraft.getInstance()
                .getBlockRenderer()
                .getModelRenderer()
                .tesselateBlock(
                        getLevel(),
                        new FaceRotatingModel(driveModel.getCellChassisModel(clientCell), orientation),
                        getBlockEntity().getBlockState(),
                        getBlockEntity().getBlockPos(),
                        poseStack,
                        buffers.getBuffer(RenderType.cutout()),
                        false,
                        RandomSource.create(),
                        0L,
                        combinedOverlayIn,
                        ModelData.EMPTY,
                        null);
        CellLedRenderer.renderLed(this, 0, buffers.getBuffer(CellLedRenderer.RENDER_LAYER), poseStack, partialTicks);

        poseStack.popPose();
    }
}

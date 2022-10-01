package ninety.megacells.item.cell.bulk;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

public class BulkCellInventory implements StorageCell {

    private static final String KEY = "key";
    private static final String COUNT = "count";

    private final ISaveProvider container;
    private final ItemStack i;
    private final IBulkCellItem cellType;

    private AEKey storedItem;
    private long itemCount;
    private IPartitionList partitionList;
    private boolean isPersisted = true;

    public BulkCellInventory(IBulkCellItem cellType, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.cellType = cellType;
        this.container = container;

        this.storedItem = getTag().contains(KEY) ? AEKey.fromTagGeneric(getTag().getCompound(KEY)) : null;
        this.itemCount = getTag().getLong(COUNT);

        var builder = IPartitionList.builder();
        var config = getConfigInventory();
        builder.addAll(config.keySet());
        this.partitionList = builder.build();
    }

    private CompoundTag getTag() {
        return this.i.getOrCreateTag();
    }

    public static BulkCellInventory createInventory(ItemStack o, ISaveProvider container) {
        Objects.requireNonNull(o, "Cannot create cell inventory for null itemstack");

        if (!(o.getItem()instanceof IBulkCellItem cellType)) {
            return null;
        }

        return new BulkCellInventory(cellType, o, container);
    }

    private static boolean isCellEmpty(BulkCellInventory inv) {
        if (inv != null) {
            return inv.getAvailableStacks().isEmpty();
        }
        return true;
    }

    @Override
    public CellState getStatus() {
        if (this.itemCount == 0) {
            return CellState.EMPTY;
        }
        if (this.itemCount == Long.MAX_VALUE) {
            return CellState.FULL;
        }
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 10.0f;
    }

    public ConfigInventory getConfigInventory() {
        return this.cellType.getConfigInventory(this.i);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !AEKeyType.items().contains(what) || !this.partitionList.isListed(what)) {
            return 0;
        }

        if (what instanceof AEItemKey itemKey) {
            var meInventory = createInventory(itemKey.toStack(), null);
            if (!isCellEmpty(meInventory)) {
                return 0;
            }
        }

        if (this.storedItem != null && !this.storedItem.equals(what)) {
            return 0;
        }

        if (this.itemCount - Long.MAX_VALUE + amount > 0) {
            // overflow
            amount = Long.MAX_VALUE - this.itemCount;
        }

        if (mode == Actionable.MODULATE) {
            if (this.storedItem == null) {
                this.storedItem = what;
            }
            this.itemCount += amount;
            saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var extractAmount = Math.min(Integer.MAX_VALUE, amount);

        var currentCount = this.itemCount;
        if (this.itemCount > 0 && Objects.equals(this.storedItem, what)) {
            if (extractAmount >= currentCount) {
                if (mode == Actionable.MODULATE) {
                    this.storedItem = null;
                    this.itemCount = 0;
                    this.saveChanges();
                }
                return currentCount;
            } else {
                if (mode == Actionable.MODULATE) {
                    this.itemCount -= extractAmount;
                    this.saveChanges();
                }
                return extractAmount;
            }
        }
        return 0;
    }

    protected void saveChanges() {
        this.isPersisted = false;
        if (this.container != null) {
            this.container.saveChanges();
        } else {
            // if there is no ISaveProvider, store to NBT immediately
            this.persist();
        }
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }

        if (this.storedItem == null || this.itemCount < 0) {
            this.getTag().remove(KEY);
            this.getTag().remove(COUNT);
        } else {
            this.getTag().put(KEY, this.storedItem.toTagGeneric());
            this.getTag().putLong(COUNT, this.itemCount);
        }

        this.isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (this.storedItem != null && this.itemCount > 0) {
            out.add(this.storedItem, this.itemCount);
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return Objects.equals(what, this.storedItem) || this.partitionList.isListed(what);
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}

package gripe._90.megacells.integration.appmek.item.cell.radioactive;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.common.registries.MekanismGases;

public class RadioactiveCellInventory implements StorageCell {

    private static final String KEY = "key";
    private static final String COUNT = "count";

    protected static final int MAX_BYTES = 256;
    private static final long MAX_MB = (long) MAX_BYTES * MekanismKeyType.TYPE.getAmountPerByte();

    private final ISaveProvider container;
    private final ItemStack i;
    private final IRadioactiveCellItem cellType;

    private AEKey storedChemical;
    private long chemAmount;
    private IPartitionList partitionList;
    private boolean isPersisted = true;

    public RadioactiveCellInventory(IRadioactiveCellItem cellType, ItemStack o, ISaveProvider container) {
        this.i = o;
        this.cellType = cellType;
        this.container = container;

        this.storedChemical = getTag().contains(KEY) ? AEKey.fromTagGeneric(getTag().getCompound(KEY)) : null;
        this.chemAmount = getTag().getLong(COUNT);

        var builder = IPartitionList.builder();
        var config = getConfigInventory();
        builder.addAll(config.keySet());
        this.partitionList = builder.build();
    }

    private CompoundTag getTag() {
        return this.i.getOrCreateTag();
    }

    public static RadioactiveCellInventory createInventory(ItemStack o, ISaveProvider container) {
        Objects.requireNonNull(o, "Cannot create cell inventory for null itemstack");

        if (!(o.getItem()instanceof IRadioactiveCellItem cellType)) {
            return null;
        }

        return new RadioactiveCellInventory(cellType, o, container);
    }

    private static boolean isCellEmpty(RadioactiveCellInventory inv) {
        if (inv != null) {
            return inv.getAvailableStacks().isEmpty();
        }
        return true;
    }

    @Override
    public CellState getStatus() {
        if (this.chemAmount == 0) {
            return CellState.EMPTY;
        }
        if (this.chemAmount == MAX_MB) {
            return CellState.FULL;
        }
        if (this.chemAmount > MAX_MB / 2) {
            return CellState.TYPES_FULL;
        }
        if (!this.storedChemical.equals(getFilterItem())) {
            return CellState.FULL;
        }
        return CellState.NOT_EMPTY;
    }

    protected AEKey getFilterItem() {
        var config = getConfigInventory().keySet().stream().toList();
        if (config.isEmpty()) {
            return null;
        } else {
            return config.get(0);
        }
    }

    protected long getUsedBytes() {
        return this.chemAmount / MekanismKeyType.TYPE.getAmountPerByte();
    }

    @Override
    public double getIdleDrain() {
        return 250.0f;
    }

    private ConfigInventory getConfigInventory() {
        return this.cellType.getConfigInventory(this.i);
    }

    protected boolean isBlackListed(AEKey what) {
        if (what instanceof MekanismKey key) {
            return ChemicalAttributeValidator.DEFAULT.process(key.getStack())
                    || key.getStack().getRaw().getChemical() == MekanismGases.SPENT_NUCLEAR_WASTE.getChemical();
        } else {
            return true;
        }
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !MekanismKeyType.TYPE.contains(what)) {
            return 0;
        }

        if (!this.partitionList.isListed(what) || isBlackListed(what)) {
            return 0;
        }

        if (what instanceof AEItemKey itemKey) {
            var meInventory = createInventory(itemKey.toStack(), null);
            if (!isCellEmpty(meInventory)) {
                return 0;
            }
        }

        if (this.storedChemical != null && !this.storedChemical.equals(what)) {
            return 0;
        }

        if (this.chemAmount == MAX_MB) {
            return 0;
        }

        long remainingAmount = Math.max(0, MAX_MB - this.chemAmount);
        if (amount > remainingAmount) {
            amount = remainingAmount;
        }

        if (mode == Actionable.MODULATE) {
            if (this.storedChemical == null) {
                this.storedChemical = what;
            }
            this.chemAmount += amount;
            saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var extractAmount = Math.min(Integer.MAX_VALUE, amount);

        var currentCount = this.chemAmount;
        if (this.chemAmount > 0 && Objects.equals(this.storedChemical, what)) {
            if (extractAmount >= currentCount) {
                if (mode == Actionable.MODULATE) {
                    this.storedChemical = null;
                    this.chemAmount = 0;
                    saveChanges();
                }
                return currentCount;
            } else {
                if (mode == Actionable.MODULATE) {
                    this.chemAmount -= extractAmount;
                    saveChanges();
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

        if (this.storedChemical == null || this.chemAmount < 0) {
            this.getTag().remove(KEY);
            this.getTag().remove(COUNT);
        } else {
            this.getTag().put(KEY, this.storedChemical.toTagGeneric());
            this.getTag().putLong(COUNT, this.chemAmount);
        }

        this.isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (this.storedChemical != null && this.chemAmount > 0) {
            out.add(this.storedChemical, this.chemAmount);
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return Objects.equals(what, this.storedChemical) || this.partitionList.isListed(what);
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}

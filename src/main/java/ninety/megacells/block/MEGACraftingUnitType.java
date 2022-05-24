package ninety.megacells.block;

import appeng.block.crafting.ICraftingUnitType;

public enum MEGACraftingUnitType implements ICraftingUnitType {
    UNIT(0, "unit"),
    ACCELERATOR(0, "accelerator"),
    STORAGE_1M(1, "1m_storage"),
    STORAGE_4M(4, "4m_storage"),
    STORAGE_16M(16, "16m_storage"),
    STORAGE_64M(64, "64m_storage"),
    STORAGE_256M(256, "256m_storage"),
    MONITOR(0, "monitor");

    private final int storageMb;
    private final String affix;

    MEGACraftingUnitType(int storageMb, String affix) {
        this.storageMb = storageMb;
        this.affix = affix;
    }

    @Override
    public int getStorageBytes() {
        return 1024 * 1024 * storageMb;
    }

    @Override
    public boolean isAccelerator() {
        return this == ACCELERATOR;
    }

    @Override
    public boolean isStatus() {
        return this == MONITOR;
    }

    public String getAffix() {
        return this.affix;
    }
}

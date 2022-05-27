package ninety.megacells.block;

import net.minecraft.world.item.Item;

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
    public int getAcceleratorThreads() {
        return this == ACCELERATOR ? 4 : 0;
    }

    @Override
    public boolean isStatus() {
        return this == MONITOR;
    }

    public String getAffix() {
        return this.affix;
    }

    @Override
    public Item getItemFromType() {
        var definition = switch (this) {
            case UNIT -> MEGABlocks.MEGA_CRAFTING_UNIT;
            case ACCELERATOR -> MEGABlocks.CRAFTING_ACCELERATOR;
            case STORAGE_1M -> MEGABlocks.CRAFTING_STORAGE_1M;
            case STORAGE_4M -> MEGABlocks.CRAFTING_STORAGE_4M;
            case STORAGE_16M -> MEGABlocks.CRAFTING_STORAGE_16M;
            case STORAGE_64M -> MEGABlocks.CRAFTING_STORAGE_64M;
            case STORAGE_256M -> MEGABlocks.CRAFTING_STORAGE_256M;
            case MONITOR -> MEGABlocks.CRAFTING_MONITOR;
        };
        return definition.asItem();
    }
}

package ninety.megacells.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import appeng.block.crafting.ICraftingUnitType;

public enum MEGACraftingUnitType implements ICraftingUnitType {
    UNIT(0, MEGABlocks.MEGA_CRAFTING_UNIT, "unit"),
    ACCELERATOR(0, MEGABlocks.CRAFTING_ACCELERATOR, "accelerator"),
    STORAGE_1M(1, MEGABlocks.CRAFTING_STORAGE_1M, "1m_storage"),
    STORAGE_4M(4, MEGABlocks.CRAFTING_STORAGE_4M, "4m_storage"),
    STORAGE_16M(16, MEGABlocks.CRAFTING_STORAGE_16M, "16m_storage"),
    STORAGE_64M(64, MEGABlocks.CRAFTING_STORAGE_64M, "64m_storage"),
    STORAGE_256M(256, MEGABlocks.CRAFTING_STORAGE_256M, "256m_storage"),
    MONITOR(0, MEGABlocks.CRAFTING_MONITOR, "monitor");

    private final int storageMb;
    private final MEGABlocks.BlockDefinition<?> craftingBlock;
    private final String affix;

    MEGACraftingUnitType(int storageMb, MEGABlocks.BlockDefinition<?> craftingBlock, String affix) {
        this.storageMb = storageMb;
        this.craftingBlock = craftingBlock;
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

    @Override
    public Item getItemFromType() {
        return this.craftingBlock != null
                ? this.craftingBlock.asItem()
                : Items.AIR;
    }
}

package ninety.megacells.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.crafting.CraftingStorageBlockEntity;

import ninety.megacells.block.MEGABlocks;

public class MEGACraftingStorageBlockEntity extends CraftingStorageBlockEntity {

    private static final int MEGA_SCALAR = 1024 * 1024;

    public MEGACraftingStorageBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    protected Item getItemFromBlockEntity() {
        var storage = getStorageBytes() / MEGA_SCALAR;

        return switch (storage) {
            case 1 -> MEGABlocks.CRAFTING_STORAGE_1M.asItem();
            case 4 -> MEGABlocks.CRAFTING_STORAGE_4M.asItem();
            case 16 -> MEGABlocks.CRAFTING_STORAGE_16M.asItem();
            case 64 -> MEGABlocks.CRAFTING_STORAGE_64M.asItem();
            case 256 -> MEGABlocks.CRAFTING_STORAGE_256M.asItem();
            default -> super.getItemFromBlockEntity();
        };
    }
}

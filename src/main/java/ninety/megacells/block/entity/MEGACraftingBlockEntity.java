package ninety.megacells.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.crafting.CraftingBlockEntity;

import ninety.megacells.block.MEGABlocks;

public class MEGACraftingBlockEntity extends CraftingBlockEntity {
    public MEGACraftingBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    protected Item getItemFromBlockEntity() {
        if (isAccelerator()) {
            return MEGABlocks.CRAFTING_ACCELERATOR.asItem();
        } else {
            return MEGABlocks.MEGA_CRAFTING_UNIT.asItem();
        }
    }
}

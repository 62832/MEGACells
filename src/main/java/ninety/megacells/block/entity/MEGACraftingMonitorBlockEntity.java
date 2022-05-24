package ninety.megacells.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.crafting.CraftingMonitorBlockEntity;

import ninety.megacells.block.MEGABlocks;

public class MEGACraftingMonitorBlockEntity extends CraftingMonitorBlockEntity {
    public MEGACraftingMonitorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    protected Item getItemFromBlockEntity() {
        return MEGABlocks.CRAFTING_MONITOR.asItem();
    }
}

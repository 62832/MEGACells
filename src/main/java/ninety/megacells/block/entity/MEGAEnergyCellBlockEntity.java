package ninety.megacells.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.networking.EnergyCellBlockEntity;

public class MEGAEnergyCellBlockEntity extends EnergyCellBlockEntity {

    private static final double MAX_STORED = 200000 * 64;

    public MEGAEnergyCellBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.setInternalMaxPower(MAX_STORED);
    }

    @Override
    public int getPriority() {
        return 9600;
    }
}

package ninety.megacells.block;

import appeng.block.crafting.AbstractCraftingUnitBlock;

import ninety.megacells.block.entity.MEGACraftingMonitorBlockEntity;

public class MEGACraftingMonitorBlock extends AbstractCraftingUnitBlock<MEGACraftingMonitorBlockEntity> {
    public MEGACraftingMonitorBlock(Properties props) {
        super(props, MEGACraftingUnitType.MONITOR);
    }
}

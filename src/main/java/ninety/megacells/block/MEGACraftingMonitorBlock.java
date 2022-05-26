package ninety.megacells.block;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.blockentity.crafting.CraftingMonitorBlockEntity;

public class MEGACraftingMonitorBlock extends AbstractCraftingUnitBlock<CraftingMonitorBlockEntity> {
    public MEGACraftingMonitorBlock(Properties props) {
        super(props, MEGACraftingUnitType.MONITOR);
    }
}

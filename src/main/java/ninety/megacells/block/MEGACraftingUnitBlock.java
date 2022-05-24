package ninety.megacells.block;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;

import ninety.megacells.block.entity.MEGACraftingBlockEntity;

public class MEGACraftingUnitBlock extends AbstractCraftingUnitBlock<MEGACraftingBlockEntity> {
    public MEGACraftingUnitBlock(Properties props, ICraftingUnitType type) {
        super(props, type);
    }
}

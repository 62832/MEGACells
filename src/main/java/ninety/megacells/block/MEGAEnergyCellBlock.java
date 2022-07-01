package ninety.megacells.block;

import appeng.block.networking.EnergyCellBlock;

public class MEGAEnergyCellBlock extends EnergyCellBlock {
    public MEGAEnergyCellBlock() {

    }

    @Override
    public double getMaxPower() {
        return 200000.0 * 64.0;
    }
}

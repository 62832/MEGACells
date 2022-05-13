package ninety.megacells.item;

import ninety.megacells.item.util.IMEGACellType;
import ninety.megacells.item.util.MEGACellTier;
import ninety.megacells.item.util.MEGACellType;

import appeng.items.storage.BasicStorageCell;

public class MEGAStorageCell extends BasicStorageCell {

    private final MEGACellTier tier;
    private final IMEGACellType type;

    public MEGAStorageCell(Properties properties, MEGACellTier tier, IMEGACellType type) {
        super(properties, tier.getComponent(), type.housing(), 2.5f + 0.5f * tier.index, tier.kbFactor(),
                tier.kbFactor() * 8, type == MEGACellType.ITEM ? 63 : 5, type.keyType());
        this.tier = tier;
        this.type = type;
    }

    public MEGACellTier getTier() {
        return this.tier;
    }

    public IMEGACellType getType() {
        return this.type;
    }

}

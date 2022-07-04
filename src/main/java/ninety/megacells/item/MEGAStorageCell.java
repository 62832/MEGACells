package ninety.megacells.item;

import appeng.items.storage.BasicStorageCell;

import ninety.megacells.core.MEGATier;
import ninety.megacells.item.core.IMEGACellType;

public class MEGAStorageCell extends BasicStorageCell {

    private final MEGATier tier;
    private final IMEGACellType type;

    public MEGAStorageCell(Properties properties, MEGATier tier, IMEGACellType type) {
        super(properties, tier.getComponent(), type.housing(), 2.5f + 0.5f * tier.index, tier.kbFactor(),
                tier.kbFactor() * 8, type.maxTypes(), type.keyType());
        this.tier = tier;
        this.type = type;
    }

    public MEGATier getTier() {
        return this.tier;
    }

    public IMEGACellType getType() {
        return this.type;
    }
}

package gripe._90.megacells.item;

import appeng.items.storage.BasicStorageCell;

import gripe._90.megacells.item.core.IMEGACellType;
import gripe._90.megacells.item.core.MEGATier;

public class MEGAStorageCell extends BasicStorageCell {

    private final MEGATier tier;
    private final IMEGACellType type;

    public MEGAStorageCell(Properties properties, MEGATier tier, IMEGACellType type) {
        super(properties.stacksTo(1), tier.getComponent(), type.housing(), 2.5f + 0.5f * tier.index, tier.kbFactor(),
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

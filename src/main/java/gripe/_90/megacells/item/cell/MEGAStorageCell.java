package gripe._90.megacells.item.cell;

import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;

public class MEGAStorageCell extends BasicStorageCell {

    private final StorageTier tier;
    private final IMEGACellType type;

    public MEGAStorageCell(Properties properties, StorageTier tier, IMEGACellType type) {
        super(properties.stacksTo(1), tier.componentSupplier().get(), type.housing(), tier.idleDrain(),
                tier.bytes() / 1024, tier.bytes() / 128, type.maxTypes(), type.keyType());
        this.tier = tier;
        this.type = type;
    }

    public StorageTier getTier() {
        return this.tier;
    }

    public IMEGACellType getType() {
        return this.type;
    }
}

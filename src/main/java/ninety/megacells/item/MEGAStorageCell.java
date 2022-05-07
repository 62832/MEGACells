package ninety.megacells.item;

import net.minecraft.world.level.ItemLike;

import appeng.items.storage.BasicStorageCell;

public class MEGAStorageCell extends BasicStorageCell {

    private final MEGACellTier tier;
    private final MEGACellType type;

    public MEGAStorageCell(Properties properties, ItemLike coreItem, MEGACellTier tier, MEGACellType type) {
        super(properties, coreItem, type.getHousing(), 2.5f + 0.5f * tier.index, tier.kbFactor(),
                tier.kbFactor() * 8, type == MEGACellType.ITEM ? 63 : 5, type.key);
        this.tier = tier;
        this.type = type;
    }

    public MEGACellTier getTier() {
        return this.tier;
    }

    public MEGACellType getType() {
        return this.type;
    }

}

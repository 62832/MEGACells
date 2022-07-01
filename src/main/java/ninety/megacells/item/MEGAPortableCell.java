package ninety.megacells.item;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;

import appeng.items.tools.powered.PortableCellItem;

import ninety.megacells.MEGACells;
import ninety.megacells.core.MEGATier;

public class MEGAPortableCell extends PortableCellItem {

    public final MEGATier tier;
    public final IMEGACellType type;

    public MEGAPortableCell(Properties props, MEGATier tier, IMEGACellType type) {
        super(type.keyType(), type.portableCellMenu(), makePortableTier(tier), props);
        this.tier = tier;
        this.type = type;
    }

    private static StorageTier makePortableTier(MEGATier tier) {
        return new StorageTier(tier.affix, 512 * tier.kbFactor(), 9 + 9 * tier.index, 2048 * tier.index,
                tier::getComponent);
    }

    @Override
    public ResourceLocation getRecipeId() {
        return MEGACells.makeId("cells/portable/" + Objects.requireNonNull(getRegistryName()).getPath());
    }
}

package ninety.megacells.integration.appmek;

import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKey;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import ninety.megacells.core.MEGATier;
import ninety.megacells.item.MEGAStorageCell;

public class MEGAChemicalCell extends MEGAStorageCell {
    public MEGAChemicalCell(Properties properties, MEGATier tier) {
        super(properties, tier, AppMekCellType.CHEMICAL);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        if (requestedAddition instanceof MekanismKey key) {
            // blacklist anything radioactive
            return !ChemicalAttributeValidator.DEFAULT.process(key.getStack());
        }
        return true;
    }
}

package gripe._90.megacells.integration.appmek;

import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKey;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import gripe._90.megacells.core.MEGATier;
import gripe._90.megacells.item.MEGAPortableCell;

public class MEGAPortableChemicalCell extends MEGAPortableCell {
    public MEGAPortableChemicalCell(Properties props, MEGATier tier) {
        super(props.stacksTo(1), tier, AppMekCellType.CHEMICAL);
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

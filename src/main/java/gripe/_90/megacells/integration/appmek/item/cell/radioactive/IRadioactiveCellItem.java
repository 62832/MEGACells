package gripe._90.megacells.integration.appmek.item.cell.radioactive;

import java.util.List;

import com.google.common.base.Preconditions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.common.registries.MekanismGases;

public interface IRadioactiveCellItem extends ICellWorkbenchItem {
    default ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(MekanismKeyType.TYPE.filter(), is);
    }

    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        RadioactiveCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }

    default boolean isBlackListed(ItemStack is, AEKey requestedAddition) {
        if (requestedAddition instanceof MekanismKey key) {
            return ChemicalAttributeValidator.DEFAULT.process(key.getStack())
                    && !key.getStack().getRaw().equals(MekanismGases.SPENT_NUCLEAR_WASTE.get());
        } else {
            return true;
        }
    }

    @Override
    default FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    default void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

    @Override
    default boolean isEditable(ItemStack is) {
        return true;
    }
}

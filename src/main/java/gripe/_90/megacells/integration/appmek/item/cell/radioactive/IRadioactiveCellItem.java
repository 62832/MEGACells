package gripe._90.megacells.integration.appmek.item.cell.radioactive;

import java.util.List;

import com.google.common.base.Preconditions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;

import me.ramidzkh.mekae2.ae2.MekanismKeyType;

public interface IRadioactiveCellItem extends ICellWorkbenchItem {
    default ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(MekanismKeyType.TYPE.filter(), is, 1);
    }

    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        RadioactiveCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
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

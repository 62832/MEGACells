package gripe._90.megacells.integration.appmek.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.items.AEBaseItem;

import gripe._90.megacells.integration.appmek.item.cell.radioactive.IRadioactiveCellItem;

public class MEGARadioactiveCell extends AEBaseItem implements IRadioactiveCellItem {
    public MEGARadioactiveCell(Properties properties) {
        super(properties.stacksTo(1));
    }

    public void appendHoverText(ItemStack is, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        addCellInformationToTooltip(is, lines);
    }

}

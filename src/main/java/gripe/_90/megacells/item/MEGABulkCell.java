package gripe._90.megacells.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import gripe._90.megacells.item.cell.bulk.IBulkCellItem;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.AEBaseItem;

public class MEGABulkCell extends AEBaseItem implements IBulkCellItem {
    public MEGABulkCell(Properties properties) {
        super(properties.stacksTo(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack is, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        addCellInformationToTooltip(is, lines);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        // placeholder for later
        return UpgradeInventories.forItem(is, 1);
    }
}

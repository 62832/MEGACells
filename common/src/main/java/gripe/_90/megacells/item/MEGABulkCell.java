package gripe._90.megacells.item;

import java.util.List;

import com.google.common.base.Preconditions;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;

import gripe._90.megacells.item.cell.BulkCellHandler;

public class MEGABulkCell extends AEBaseItem implements ICellWorkbenchItem {
    public MEGABulkCell(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(AEItemKey.filter(), is, 1);
    }

    @Override
    public void appendHoverText(ItemStack is, Level level, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        Preconditions.checkArgument(is.getItem() == this);
        BulkCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {
    }
}

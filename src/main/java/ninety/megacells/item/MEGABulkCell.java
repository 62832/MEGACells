package ninety.megacells.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.me.cells.BasicCellHandler;
import appeng.util.ConfigInventory;

public class MEGABulkCell extends AEBaseItem implements IBasicCellItem {
    public MEGABulkCell(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack is, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        var handler = BasicCellHandler.INSTANCE.getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        double used = handler.getUsedBytes();
        lines.add(used == 0 ? Tooltips.of("Empty")
                : Tooltips.of(Tooltips.of("Capacity Used: "),
                        Tooltips.ofPercent(used / (double) Integer.MAX_VALUE, false)));

        var containedType = handler.getAvailableStacks().getFirstKey();
        if (containedType != null) {
            var item = containedType.wrapForDisplayOrFilter();
            lines.add(Tooltips.of(Tooltips.of("Contains: "), Tooltips.of(item.getHoverName())));
        }
    }

    @Override
    public AEKeyType getKeyType() {
        return AEKeyType.items();
    }

    @Override
    public int getBytes(ItemStack is) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBytesPerType(ItemStack is) {
        return 1;
    }

    @Override
    public int getTotalTypes(ItemStack is) {
        return 1;
    }

    @Override
    public double getIdleDrain() {
        return 10.0f;
    }

    @Override
    public boolean isEditable(ItemStack is) {
        return true;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(AEItemKey.filter(), is);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }
}

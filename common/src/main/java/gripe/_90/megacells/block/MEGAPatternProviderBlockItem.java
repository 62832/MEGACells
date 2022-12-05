package gripe._90.megacells.block;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import appeng.block.AEBaseBlockItem;
import appeng.core.localization.Tooltips;

public class MEGAPatternProviderBlockItem extends AEBaseBlockItem {
    public MEGAPatternProviderBlockItem(Block id, Item.Properties props) {
        super(id, props);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.addCheckedInformation(stack, level, tooltip, flag);
        tooltip.add(Tooltips.of("Supports processing patterns only."));
    }
}

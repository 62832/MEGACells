package gripe._90.megacells.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import appeng.block.AEBaseBlockItem;
import appeng.core.localization.Tooltips;

import gripe._90.megacells.definition.MEGATranslations;

public class MEGAPatternProviderBlockItem extends AEBaseBlockItem {
    public MEGAPatternProviderBlockItem(Block id, Properties props) {
        super(id, props);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Tooltips.of(MEGATranslations.ProcessingOnly.text()));
    }
}

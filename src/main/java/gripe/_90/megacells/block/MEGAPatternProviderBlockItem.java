package gripe._90.megacells.block;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import appeng.block.AEBaseBlockItem;

import gripe._90.megacells.definition.MEGATranslations;

public class MEGAPatternProviderBlockItem extends AEBaseBlockItem {
    public static final Style NOTICE = Style.EMPTY.withColor(0xffde7d).withItalic(true);

    public MEGAPatternProviderBlockItem(Block id, Properties props) {
        super(id, props);
    }

    @Override
    public void addCheckedInformation(
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advTooltips) {
        lines.add(MEGATranslations.ProcessingOnly.text().withStyle(NOTICE));
    }
}

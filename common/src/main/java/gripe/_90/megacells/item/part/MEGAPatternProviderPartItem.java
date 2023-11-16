package gripe._90.megacells.item.part;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.items.parts.PartItem;

import gripe._90.megacells.block.MEGAPatternProviderBlockItem;
import gripe._90.megacells.definition.MEGATranslations;

public class MEGAPatternProviderPartItem extends PartItem<MEGAPatternProviderPart> {
    public MEGAPatternProviderPartItem(Properties properties) {
        super(properties, MEGAPatternProviderPart.class, MEGAPatternProviderPart::new);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(MEGATranslations.ProcessingOnly.text().withStyle(MEGAPatternProviderBlockItem.NOTICE));
    }
}

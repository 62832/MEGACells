package gripe._90.megacells.item.part;

import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import appeng.items.parts.PartItem;

import gripe._90.megacells.block.MEGAPatternProviderBlockItem;
import gripe._90.megacells.definition.MEGATranslations;

public class MEGAPatternProviderPartItem extends PartItem<MEGAPatternProviderPart> {
    public MEGAPatternProviderPartItem(Properties properties) {
        super(properties, MEGAPatternProviderPart.class, MEGAPatternProviderPart::new);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> lines,
            TooltipFlag flag) {
        lines.accept(MEGATranslations.ProcessingOnly.text().withStyle(MEGAPatternProviderBlockItem.NOTICE));
    }
}

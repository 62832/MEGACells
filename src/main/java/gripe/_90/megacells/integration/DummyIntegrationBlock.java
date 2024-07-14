package gripe._90.megacells.integration;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;

import gripe._90.megacells.definition.MEGATranslations;

public class DummyIntegrationBlock extends AEBaseBlock {
    public DummyIntegrationBlock(Properties props) {
        super(props);
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {}

    public static class Item extends AEBaseBlockItem {
        private final Addons addon;

        public Item(Block block, Properties props, Addons addon) {
            super(block, props);
            this.addon = addon;
        }

        @Override
        public void addCheckedInformation(
                ItemStack itemStack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
            lines.add(MEGATranslations.NotInstalled.text(addon.getModName()).withStyle(ChatFormatting.GRAY));
        }
    }
}

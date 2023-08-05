package gripe._90.megacells.item;

import static gripe._90.megacells.definition.MEGABlocks.CRAFTING_ACCELERATOR;
import static gripe._90.megacells.definition.MEGABlocks.MEGA_CRAFTING_UNIT;
import static gripe._90.megacells.definition.MEGATranslations.AcceleratorThreads;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import appeng.block.crafting.CraftingBlockItem;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.util.InteractionUtil;

public class MEGACraftingBlockItem extends CraftingBlockItem {
    public MEGACraftingBlockItem(Block id, Properties props, Supplier<ItemLike> disassemblyExtra) {
        super(id, props, disassemblyExtra);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (AEConfig.instance().isDisassemblyCraftingEnabled() && InteractionUtil.isInAlternateUseMode(player)) {
            int itemCount = player.getItemInHand(hand).getCount();
            player.setItemInHand(hand, ItemStack.EMPTY);

            player.getInventory().placeItemBackInInventory(MEGA_CRAFTING_UNIT.stack(itemCount));
            player.getInventory().placeItemBackInInventory(new ItemStack(disassemblyExtra.get(), itemCount));

            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }

        return super.use(level, player, hand);
    }

    @Override
    public void addCheckedInformation(ItemStack itemStack, Level level, List<Component> lines, TooltipFlag flag) {
        if (this.getBlock().equals(CRAFTING_ACCELERATOR.block())) {
            lines.add(Tooltips.of(AcceleratorThreads.text()));
        }
    }
}

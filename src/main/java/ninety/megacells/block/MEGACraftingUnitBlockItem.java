package ninety.megacells.block;

import java.util.function.Supplier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import appeng.block.crafting.CraftingBlockItem;
import appeng.core.AEConfig;
import appeng.util.InteractionUtil;

public class MEGACraftingUnitBlockItem extends CraftingBlockItem {

    public MEGACraftingUnitBlockItem(Block id, Properties props, Supplier<ItemLike> disassemblyExtra) {
        super(id, props, disassemblyExtra);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (AEConfig.instance().isDisassemblyCraftingEnabled() && InteractionUtil.isInAlternateUseMode(player)) {
            int itemCount = player.getItemInHand(hand).getCount();
            player.setItemInHand(hand, ItemStack.EMPTY);

            player.getInventory().placeItemBackInInventory(MEGABlocks.MEGA_CRAFTING_UNIT.stack(itemCount));
            player.getInventory().placeItemBackInInventory(new ItemStack(disassemblyExtra.get(), itemCount));

            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }
}

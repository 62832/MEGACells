package gripe._90.megacells.integration.appbot;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import appeng.api.storage.StorageCells;
import appeng.items.storage.StorageTier;
import appeng.util.InteractionUtil;

import appbot.item.ManaCellItem;

public class MEGAManaCellItem extends ManaCellItem {
    private final ItemLike coreItem;

    public MEGAManaCellItem(Properties properties, StorageTier tier) {
        super(properties, tier.componentSupplier().get(), tier.bytes() / 1024, tier.idleDrain());
        this.coreItem = tier.componentSupplier().get();
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(
            @NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        disassembleDrive(player.getItemInHand(hand), level, player);
        return new InteractionResultHolder<>(
                InteractionResult.sidedSuccess(level.isClientSide()), player.getItemInHand(hand));
    }

    @NotNull
    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        return disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }

    private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            if (level.isClientSide()) {
                return false;
            }

            var playerInventory = player.getInventory();
            var inv = StorageCells.getCellInventory(stack, null);
            if (inv != null && playerInventory.getSelected() == stack) {
                var list = inv.getAvailableStacks();

                if (list.isEmpty()) {
                    playerInventory.setItem(playerInventory.selected, ItemStack.EMPTY);
                    playerInventory.placeItemBackInInventory(coreItem.asItem().getDefaultInstance());
                    playerInventory.placeItemBackInInventory(AppBotItems.MEGA_MANA_CELL_HOUSING.stack());
                    return true;
                }
            }
        }

        return false;
    }
}

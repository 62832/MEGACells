package gripe._90.megacells.integration.appliede;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;

public class MEGAEMCInterfaceBlock extends AEBaseEntityBlock<MEGAEMCInterfaceBlockEntity> {
    public MEGAEMCInterfaceBlock() {
        super(metalProps());
    }

    @Override
    public InteractionResult onActivated(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            @Nullable ItemStack heldItem,
            BlockHitResult hit) {
        if (!InteractionUtil.isInAlternateUseMode(player)) {
            var be = getBlockEntity(level, pos);

            if (be != null) {
                if (!level.isClientSide()) {
                    be.openMenu(player, MenuLocators.forBlockEntity(be));
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }
}

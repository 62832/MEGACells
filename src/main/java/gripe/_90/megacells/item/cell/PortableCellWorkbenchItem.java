package gripe._90.megacells.item.cell;

import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.stacks.GenericStack;
import appeng.core.AEConfig;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import gripe._90.megacells.definition.MEGAMenus;

public class PortableCellWorkbenchItem extends AEBaseItem implements IMenuItem {
    public PortableCellWorkbenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Nullable
    @Override
    public ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new PortableCellWorkbenchMenuHost(this, player, locator);
    }

    @ParametersAreNonnullByDefault
    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            MenuOpener.open(MEGAMenus.PORTABLE_CELL_WORKBENCH.get(), player, MenuLocators.forHand(player, hand));
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        var host = new PortableCellWorkbenchMenuHost(this, null, MenuLocators.forStack(stack));
        var config = host.getConfig().toList();

        var shownConfig = new ArrayList<GenericStack>();
        var hasMore = false;

        for (var c : config) {
            if (c != null) {
                shownConfig.add(c);

                if (shownConfig.size() == AEConfig.instance().getTooltipMaxCellContentShown()) {
                    hasMore = true;
                    break;
                }
            }
        }

        return Optional.of(
                new PortableCellWorkbenchTooltipComponent(shownConfig, host.mega$getContainedStack(), hasMore));
    }
}

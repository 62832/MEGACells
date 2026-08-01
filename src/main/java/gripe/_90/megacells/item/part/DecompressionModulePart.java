package gripe._90.megacells.item.part;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import appeng.api.networking.GridFlags;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.helpers.IPriorityHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.PriorityMenu;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.misc.DecompressionService;

public class DecompressionModulePart extends AEBasePart implements IPriorityHost {
    public DecompressionModulePart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).setIdlePowerUsage(10.0);
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!player.level().isClientSide()) {
            MenuOpener.open(PriorityMenu.TYPE, player, MenuLocators.forPart(this));
        }

        return true;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
    }

    @Override
    public int getPriority() {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getService(DecompressionService.class).getPatternPriority() : 0;
    }

    @Override
    public void setPriority(int priority) {
        var node = getMainNode().getNode();

        if (node != null) {
            node.getGrid().getService(DecompressionService.class).setPatternPriority(priority, node);
            getHost().markForSave();
        }
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAItems.DECOMPRESSION_MODULE.stack();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(3, 3, 12, 13, 13, 16);
        bch.addBox(5, 5, 11, 11, 11, 12);
    }
}

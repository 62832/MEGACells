package gripe._90.megacells.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.stacks.AEItemKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.iface.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

public class MEGAPatternProviderBlockEntity extends PatternProviderBlockEntity {

    public MEGAPatternProviderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, 18);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(MEGAPatternProviderMenu.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(MEGAPatternProviderMenu.TYPE, player, subMenu.getLocator());
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(MEGABlocks.MEGA_PATTERN_PROVIDER.stack());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGABlocks.MEGA_PATTERN_PROVIDER.stack();
    }
}

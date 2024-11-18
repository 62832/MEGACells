package gripe._90.megacells.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.CellWorkbenchScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.CellWorkbenchMenu;

import gripe._90.megacells.client.screen.CompressionCutoffButton;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.menu.CompressionCutoffHost;
import gripe._90.megacells.misc.CellWorkbenchHost;

@Mixin(CellWorkbenchScreen.class)
public abstract class CellWorkbenchScreenMixin extends AEBaseScreen<CellWorkbenchMenu> {
    @Unique
    private CompressionCutoffButton mega$compressionCutoff;

    public CellWorkbenchScreenMixin(
            CellWorkbenchMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCutoffButton(
            CellWorkbenchMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        mega$compressionCutoff = addToLeftToolbar(
                new CompressionCutoffButton(button -> ((CompressionCutoffHost) menu).mega$nextCompressionLimit()));
    }

    @Inject(method = "updateBeforeRender", at = @At("RETURN"))
    private void updateCutoffButton(CallbackInfo ci) {
        if (BulkCellItem.HANDLER.getCellInventory(((CellWorkbenchHost) menu.getHost()).mega$getContainedStack(), null)
                instanceof BulkCellInventory bulkCell) {
            mega$compressionCutoff.setVisibility(bulkCell.isCompressionEnabled()
                    && !bulkCell.getCompressionChain().isEmpty());
            mega$compressionCutoff.setItem(bulkCell.getCompressionChain().get(bulkCell.getCompressionCutoff() - 1));
        } else {
            mega$compressionCutoff.setVisibility(false);
        }
    }
}

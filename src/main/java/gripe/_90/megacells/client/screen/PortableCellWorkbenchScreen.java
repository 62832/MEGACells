package gripe._90.megacells.client.screen;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.ActionItems;
import appeng.api.config.CopyMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.menu.PortableCellWorkbenchMenu;

/**
 * See {@link appeng.client.gui.implementations.CellWorkbenchScreen}
 */
public class PortableCellWorkbenchScreen extends UpgradeableScreen<PortableCellWorkbenchMenu> {
    private final ToggleButton copyMode;
    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final CompressionCutoffButton compressionCutoff;

    public PortableCellWorkbenchScreen(
            PortableCellWorkbenchMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        fuzzyMode = addToLeftToolbar(new SettingToggleButton<>(
                Settings.FUZZY_MODE,
                FuzzyMode.IGNORE_ALL,
                (button, backwards) -> menu.setCellFuzzyMode(button.getNextValue(backwards))));
        addToLeftToolbar(new ActionButton(ActionItems.COG, act -> menu.partition()));
        addToLeftToolbar(new ActionButton(ActionItems.CLOSE, act -> menu.clear()));
        copyMode = addToLeftToolbar(new ToggleButton(
                Icon.COPY_MODE_ON,
                Icon.COPY_MODE_OFF,
                GuiText.CopyMode.text(),
                GuiText.CopyModeDesc.text(),
                act -> menu.nextWorkBenchCopyMode()));
        compressionCutoff = addToLeftToolbar(new CompressionCutoffButton(button -> menu.mega$nextCompressionLimit()));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        copyMode.setState(menu.getCopyMode() == CopyMode.CLEAR_ON_REMOVE);
        fuzzyMode.set(menu.getFuzzyMode());
        fuzzyMode.setVisibility(menu.getUpgrades().isInstalled(AEItems.FUZZY_CARD));

        if (BulkCellItem.HANDLER.getCellInventory(menu.getHost().mega$getContainedStack(), null)
                instanceof BulkCellInventory bulkCell) {
            compressionCutoff.setVisibility(bulkCell.isCompressionEnabled()
                    && !bulkCell.getCompressionChain().isEmpty());
            compressionCutoff.setItem(bulkCell.getCompressionChain().get(bulkCell.getCompressionCutoff() - 1));
        } else {
            compressionCutoff.setVisibility(false);
        }
    }

    @NotNull
    @Override
    protected List<Component> getTooltipFromContainerItem(@NotNull ItemStack stack) {
        var cell = getMenu().getWorkbenchItem();

        if (cell.isEmpty()) {
            return super.getTooltipFromContainerItem(stack);
        }

        if (cell == stack) {
            return super.getTooltipFromContainerItem(stack);
        }

        var genericStack = GenericStack.unwrapItemStack(stack);
        var what = genericStack != null ? genericStack.what() : AEItemKey.of(stack);

        if (what == null) {
            return super.getTooltipFromContainerItem(stack);
        }

        var configInventory = getMenu().getHost().getCell().getConfigInventory(cell);

        if (!configInventory.isSupportedType(what.getType())) {
            var lines = new ArrayList<>(super.getTooltipFromContainerItem(stack));
            lines.add(GuiText.IncompatibleWithCell.text().withStyle(ChatFormatting.RED));
            return lines;
        }

        var filter = configInventory.getFilter();

        if (filter != null) {
            var anySlotMatches = false;

            for (var i = 0; i < configInventory.size(); i++) {
                if (configInventory.isAllowedIn(i, what)) {
                    anySlotMatches = true;
                    break;
                }
            }

            if (!anySlotMatches) {
                var lines = new ArrayList<>(super.getTooltipFromContainerItem(stack));
                lines.add(GuiText.IncompatibleWithCell.text().withStyle(ChatFormatting.RED));
                return lines;
            }
        }

        return super.getTooltipFromContainerItem(stack);
    }
}

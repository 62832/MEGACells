package gripe._90.megacells.item.cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.util.ConfigInventory;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;

public class BulkCellItem extends AEBaseItem implements ICellWorkbenchItem {
    private static final ICellHandler HANDLER = new Handler();

    public BulkCellItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void registerHandler() {
        StorageCells.addCellHandler(HANDLER);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(Set.of(AEKeyType.items()), is, 1);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(ItemStack is, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        var inv = (BulkCellInventory) HANDLER.getCellInventory(is, null);

        if (inv != null) {
            var storedItem = inv.getStoredItem();
            var filterItem = inv.getFilterItem();

            if (storedItem != null) {
                lines.add(Tooltips.of(MEGATranslations.Contains.text(storedItem.getDisplayName())));
                var quantity = inv.getStoredQuantity();
                lines.add(Tooltips.of(MEGATranslations.Quantity.text(
                        quantity < Long.MAX_VALUE
                                ? Tooltips.ofNumber(quantity)
                                : MEGATranslations.ALot.text().withStyle(Tooltips.NUMBER_TEXT))));
            } else {
                lines.add(Tooltips.of(MEGATranslations.Empty.text()));
            }

            if (filterItem != null) {
                if (storedItem == null) {
                    lines.add(Tooltips.of(MEGATranslations.PartitionedFor.text(filterItem.getDisplayName())));
                } else if (!storedItem.equals(filterItem)) {
                    lines.add(MEGATranslations.MismatchedFilter.text(filterItem.getDisplayName())
                            .withStyle(ChatFormatting.DARK_RED));
                }
            } else {
                lines.add(
                        storedItem != null
                                ? MEGATranslations.MismatchedFilter.text(MEGATranslations.Empty.text())
                                        .withStyle(ChatFormatting.DARK_RED)
                                : Tooltips.of(MEGATranslations.NotPartitioned.text()));
            }

            lines.add(Tooltips.of(MEGATranslations.Compression.text(
                    inv.isCompressionEnabled()
                            ? MEGATranslations.Enabled.text().withStyle(ChatFormatting.GREEN)
                            : MEGATranslations.Disabled.text().withStyle(ChatFormatting.RED))));

            var trace = inv.getTraceUnits();

            if (trace > 0) {
                lines.add(Tooltips.of(
                                inv.isCompressionEnabled()
                                        ? MEGATranslations.TraceUnits.text(
                                                Tooltips.ofNumber(trace),
                                                inv.getLowestVariant().getDisplayName())
                                        : MEGATranslations.ContainsTraceUnits.text())
                        .withStyle(ChatFormatting.GOLD));
            }

            if (inv.isCompressionEnabled()) {
                var cutoffItem = inv.getCutoffItem();

                if (cutoffItem != inv.getHighestVariant()) {
                    lines.add(Tooltips.of(MEGATranslations.Cutoff.text(cutoffItem.getDisplayName())));
                }
            }
        }
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack is) {
        var inv = (BulkCellInventory) HANDLER.getCellInventory(is, null);

        if (inv == null) {
            return Optional.empty();
        }

        var upgrades = new ArrayList<ItemStack>();
        var content = new ArrayList<GenericStack>();

        if (AEConfig.instance().isTooltipShowCellUpgrades() && inv.isCompressionEnabled()) {
            upgrades.add(MEGAItems.COMPRESSION_CARD.stack());
        }

        if (AEConfig.instance().isTooltipShowCellContent()) {
            if (inv.getStoredItem() != null) {
                content.add(new GenericStack(inv.getStoredItem(), inv.getStoredQuantity()));
            } else if (inv.getFilterItem() != null) {
                content.add(new GenericStack(inv.getFilterItem(), 0));
            }
        }

        return Optional.of(new StorageCellTooltipComponent(upgrades, content, false, true));
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {}

    private static class Handler implements ICellHandler {
        private Handler() {}

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.is(MEGAItems.BULK_ITEM_CELL.asItem());
        }

        @Nullable
        @Override
        public BulkCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
            return isCell(is) ? new BulkCellInventory(is, host) : null;
        }
    }
}

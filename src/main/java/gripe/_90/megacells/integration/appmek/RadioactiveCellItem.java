package gripe._90.megacells.integration.appmek;

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
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.util.ConfigInventory;

import me.ramidzkh.mekae2.ae2.MekanismKeyType;

import gripe._90.megacells.definition.MEGATranslations;

// TODO: Writing up an in-game guide page for this cell has reminded me again just how fucking boring the cell is right
//  now. Need to look into how to make it more "interesting" to work with and deal with in 1.22+.
//  Also, maybe get rid of some of the constraints that were initially borrowed from the Bulk Cell; requiring this cell
//  to be partitioned in advance, for example, is kind of pointless.
public class RadioactiveCellItem extends AEBaseItem implements ICellWorkbenchItem {
    public static final Handler HANDLER = new Handler();

    public RadioactiveCellItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(Set.of(MekanismKeyType.TYPE), is, 1);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {}

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(ItemStack is, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        var inv = HANDLER.getCellInventory(is, null);

        if (inv != null) {
            var containedType = inv.getAvailableStacks().getFirstKey();
            var filterItem = inv.getFilterChemical();

            lines.add(Tooltips.bytesUsed(inv.getUsedBytes(), RadioactiveCellInventory.MAX_BYTES));
            lines.add(Tooltips.of(
                    containedType != null
                            ? MEGATranslations.Contains.text(containedType.getDisplayName())
                            : MEGATranslations.Empty.text()));

            if (filterItem != null) {
                if (containedType == null) {
                    lines.add(Tooltips.of(MEGATranslations.PartitionedFor.text(filterItem.getDisplayName())));
                } else if (!containedType.equals(filterItem)) {
                    lines.add(MEGATranslations.MismatchedFilter.text().withStyle(ChatFormatting.DARK_RED));
                }

                if (inv.isBlackListed(filterItem)) {
                    lines.add(MEGATranslations.FilterChemicalUnsupported.text().withStyle(ChatFormatting.DARK_RED));
                }
            } else {
                lines.add(Tooltips.of(MEGATranslations.NotPartitioned.text()));
            }
        }
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack is) {
        var inv = HANDLER.getCellInventory(is, null);

        if (inv == null) {
            return Optional.empty();
        }

        var content = new ArrayList<GenericStack>();

        if (AEConfig.instance().isTooltipShowCellContent()) {
            if (inv.getStoredChemical() != null) {
                content.add(new GenericStack(inv.getStoredChemical(), inv.getChemAmount()));
            } else if (inv.getFilterChemical() != null) {
                content.add(new GenericStack(inv.getFilterChemical(), 0));
            }
        }

        return Optional.of(new StorageCellTooltipComponent(List.of(), content, false, true));
    }

    public static class Handler implements ICellHandler {
        private Handler() {}

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.getItem() instanceof RadioactiveCellItem;
        }

        @Nullable
        @Override
        public RadioactiveCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
            return isCell(is) ? new RadioactiveCellInventory(is, host) : null;
        }
    }
}

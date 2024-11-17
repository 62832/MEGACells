package gripe._90.megacells.item.cell;

import java.util.List;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.GenericStack;

public record PortableCellWorkbenchTooltipComponent(List<GenericStack> config, ItemStack cell, boolean hasMoreConfig)
        implements TooltipComponent {}

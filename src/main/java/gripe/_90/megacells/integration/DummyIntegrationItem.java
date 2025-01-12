package gripe._90.megacells.integration;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import appeng.items.AEBaseItem;

public class DummyIntegrationItem extends AEBaseItem {
    private final Addons addon;

    public DummyIntegrationItem(Properties properties, Addons addon) {
        super(properties);
        this.addon = addon;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(addon.getUnavailableTooltip());
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {}
}

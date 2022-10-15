package gripe._90.megacells.block;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import appeng.block.AEBaseBlockItemChargeable;
import appeng.core.localization.Tooltips;

public class MEGAEnergyCellBlockItem extends AEBaseBlockItemChargeable {

    // TODO: stop being lame and PR a proper energy cell refactor
    // (EDIT 10/10/22: good job retard you forgot this)
    public MEGAEnergyCellBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, Level level, List<Component> lines,
            TooltipFlag advancedTooltips) {
        double internalCurrentPower = 0;

        final CompoundTag tag = stack.getTag();
        if (tag != null) {
            internalCurrentPower = tag.getDouble("internalCurrentPower");
        }
        lines.add(Tooltips.energyStorageComponent(internalCurrentPower, 64 * 200000));
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 3200d;
    }
}

package gripe._90.megacells.item;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import appeng.items.tools.powered.PortableCellItem;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.core.IMEGACellType;
import gripe._90.megacells.item.core.MEGATier;

public class MEGAPortableCell extends PortableCellItem {

    public final MEGATier tier;
    public final IMEGACellType type;

    public MEGAPortableCell(Properties props, MEGATier tier, IMEGACellType type) {
        super(type.keyType(), type.portableCellMenu(), makePortableTier(tier), props.stacksTo(1));
        this.tier = tier;
        this.type = type;
    }

    private static StorageTier makePortableTier(MEGATier tier) {
        return new StorageTier(tier.affix, 512 * tier.kbFactor(), 9 + 9 * tier.index, 2048 * tier.index,
                tier::getComponent);
    }

    @Override
    public ResourceLocation getRecipeId() {
        return MEGACells.makeId("cells/portable/" + Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return super.getChargeRate(stack) * 2;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return super.getAEMaxPower(stack) * 8;
    }
}

package gripe._90.megacells.item.cell;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.PortableCellItem;

import gripe._90.megacells.MEGACells;

public class MEGAPortableCell extends PortableCellItem {

    private final StorageTier tier;
    private final IMEGACellType type;

    public MEGAPortableCell(Properties props, StorageTier tier, IMEGACellType type) {
        super(type.keyType(), type.portableCellMenu(), tier, props.stacksTo(1));
        this.tier = tier;
        this.type = type;
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return 18 + this.tier.index() * 9;
    }

    @Override
    public double getIdleDrain() {
        return 1.0;
    }

    @Override
    public StorageTier getTier() {
        return this.tier;
    }

    public IMEGACellType getType() {
        return this.type;
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

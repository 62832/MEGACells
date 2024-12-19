package gripe._90.megacells.item.cell;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKeyType;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.PortableCellItem;
import appeng.menu.me.common.MEStorageMenu;

import gripe._90.megacells.MEGACells;

public class MEGAPortableCell extends PortableCellItem {
    private final StorageTier tier;

    public MEGAPortableCell(
            Properties props, StorageTier tier, AEKeyType keyType, MenuType<MEStorageMenu> menu, int defaultColour) {
        super(
                keyType,
                18 + (keyType.equals(AEKeyType.items()) ? (tier.index() - 5) * 9 : 0),
                menu,
                tier,
                props.stacksTo(1),
                defaultColour);
        this.tier = tier;
    }

    @Override
    public double getIdleDrain() {
        return 1.0;
    }

    @Override
    public StorageTier getTier() {
        return tier;
    }

    @Override
    public ResourceLocation getRecipeId() {
        return MEGACells.makeId(
                "cells/portable/" + Objects.requireNonNull(getRegistryName()).getPath());
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

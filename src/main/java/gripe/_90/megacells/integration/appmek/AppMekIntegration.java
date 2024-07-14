package gripe._90.megacells.integration.appmek;

import java.util.List;
import java.util.function.Function;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKey;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.storage.StorageTier;

import me.ramidzkh.mekae2.AMItems;
import me.ramidzkh.mekae2.AMMenus;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.item.ChemicalStorageCell;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.MEGAPortableCell;

public final class AppMekIntegration {
    public static Function<Item.Properties, Item> createChemCell(StorageTier tier) {
        return p -> new ChemicalStorageCell(p, tier, MEGAItems.MEGA_CHEMICAL_CELL_HOUSING);
    }

    public static Function<Item.Properties, Item> createChemPortable(StorageTier tier) {
        return p -> new MEGAPortableCell(p, tier, MekanismKeyType.TYPE, AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, 0x80caff) {
            @Override
            public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
                return !(requestedAddition instanceof MekanismKey key)
                        || !ChemicalAttributeValidator.DEFAULT.process(key.getStack());
            }
        };
    }

    public static void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        MEGAItems.getChemicalCells().forEach(cell -> {
            Upgrades.add(AEItems.INVERTER_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, cell, 1, storageCellGroup);
        });

        MEGAItems.getChemicalPortables().forEach(portable -> {
            Upgrades.add(AEItems.INVERTER_CARD, portable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, portable, 1, portableCellGroup);
            Upgrades.add(AEItems.VOID_CARD, portable, 1, portableCellGroup);
        });

        for (var portable : List.of(
                AMItems.PORTABLE_CHEMICAL_CELL_1K,
                AMItems.PORTABLE_CHEMICAL_CELL_4K,
                AMItems.PORTABLE_CHEMICAL_CELL_16K,
                AMItems.PORTABLE_CHEMICAL_CELL_64K,
                AMItems.PORTABLE_CHEMICAL_CELL_256K)) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable.get(), 2, portableCellGroup);
        }
    }
}

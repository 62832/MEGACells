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
import gripe._90.megacells.integration.IntegrationHelper;
import gripe._90.megacells.item.cell.MEGAPortableCell;

public final class AppMekIntegration implements IntegrationHelper {
    @Override
    public Function<Item.Properties, Item> createCell(StorageTier tier) {
        return p -> new ChemicalStorageCell(p, tier, MEGAItems.MEGA_CHEMICAL_CELL_HOUSING);
    }

    @Override
    public Function<Item.Properties, Item> createPortable(StorageTier tier) {
        return p -> new MEGAPortableCell(p, tier, MekanismKeyType.TYPE, AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, 0x80caff) {
            @Override
            public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
                return !(requestedAddition instanceof MekanismKey key)
                        || !ChemicalAttributeValidator.DEFAULT.process(key.getStack());
            }
        };
    }

    @Override
    public void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.keyType().equals("chemical")) {
                Upgrades.add(AEItems.INVERTER_CARD, cell.item(), 1, storageCellGroup);
                Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell.item(), 1, storageCellGroup);
                Upgrades.add(AEItems.VOID_CARD, cell.item(), 1, storageCellGroup);

                if (cell.portable()) {
                    Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, cell.item(), 2, portableCellGroup);
                }
            }
        }

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

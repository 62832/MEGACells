package gripe._90.megacells.integration.forge.appmek;

import java.util.List;

import net.minecraftforge.fml.ModList;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import me.ramidzkh.mekae2.AMItems;

import gripe._90.megacells.item.MEGAItems;

public final class AppMekIntegration {
    public static boolean isAppMekLoaded() {
        return ModList.get().isLoaded("appmek");
    }

    public static void initEnergyUpgrades() {
        if (isAppMekLoaded()) {
            for (var portableCell : List.of(AMItems.PORTABLE_CHEMICAL_CELL_1K, AMItems.PORTABLE_CHEMICAL_CELL_4K,
                    AMItems.PORTABLE_CHEMICAL_CELL_16K, AMItems.PORTABLE_CHEMICAL_CELL_64K,
                    AMItems.PORTABLE_CHEMICAL_CELL_256K)) {
                Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portableCell.get(), 2,
                        GuiText.PortableCells.getTranslationKey());
            }
        }
    }
}

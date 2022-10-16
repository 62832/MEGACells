package gripe._90.megacells.integration.ae2wt;

import net.minecraftforge.fml.ModList;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import gripe._90.megacells.item.MEGAItems;

public final class AE2WTIntegration {
    public static boolean isAE2WTLoaded() {
        return ModList.get().isLoaded("ae2wtlib");
    }

    public static void initEnergyUpgrades() {
        if (isAE2WTLoaded()) {
            var terminals = GuiText.WirelessTerminals.getTranslationKey();
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AE2wtlib.PATTERN_ENCODING_TERMINAL, 2, terminals);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AE2wtlib.PATTERN_ACCESS_TERMINAL, 2, terminals);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AE2wtlib.UNIVERSAL_TERMINAL, WUTHandler.getUpgradeCardCount());
        }
    }
}

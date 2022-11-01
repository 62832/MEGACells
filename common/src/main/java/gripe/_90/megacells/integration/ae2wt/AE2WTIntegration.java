package gripe._90.megacells.integration.ae2wt;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import gripe._90.megacells.item.MEGAItems;

public class AE2WTIntegration {

    public static void initIntegration() {
        var terminals = GuiText.WirelessTerminals.getTranslationKey();
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AE2wtlib.PATTERN_ENCODING_TERMINAL, 2, terminals);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AE2wtlib.PATTERN_ACCESS_TERMINAL, 2, terminals);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AE2wtlib.UNIVERSAL_TERMINAL, WUTHandler.getUpgradeCardCount());
    }
}

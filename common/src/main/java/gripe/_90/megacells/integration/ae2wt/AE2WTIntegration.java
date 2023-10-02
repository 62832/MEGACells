package gripe._90.megacells.integration.ae2wt;

import static gripe._90.megacells.definition.MEGAItems.GREATER_ENERGY_CARD;

import gripe._90.megacells.util.Utils;

import de.mari_023.ae2wtlib.UpgradeHelper;

public final class AE2WTIntegration {
    public static void initUpgrades() {
        UpgradeHelper.addUpgradeToAllTerminals(GREATER_ENERGY_CARD, 0);
        Utils.LOGGER.info("Initialised AE2WT integration.");
    }
}

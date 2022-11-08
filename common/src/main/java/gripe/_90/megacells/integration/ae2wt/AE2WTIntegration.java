package gripe._90.megacells.integration.ae2wt;

import de.mari_023.ae2wtlib.UpgradeHelper;

import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.util.Services;

public class AE2WTIntegration {
    public static void initEnergyUpgrades() {
        if (Services.PLATFORM.isModLoaded("ae2wtlib")) {
            UpgradeHelper.addUpgradeToAllTerminals(MEGAItems.GREATER_ENERGY_CARD, 0);
        }
    }
}

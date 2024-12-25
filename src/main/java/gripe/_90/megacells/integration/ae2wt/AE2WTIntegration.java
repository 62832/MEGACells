package gripe._90.megacells.integration.ae2wt;

import de.mari_023.ae2wtlib.api.registration.UpgradeHelper;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public final class AE2WTIntegration implements IntegrationHelper {
    @Override
    public void initUpgrades() {
        UpgradeHelper.addUpgradeToAllTerminals(MEGAItems.GREATER_ENERGY_CARD, 0);
    }
}

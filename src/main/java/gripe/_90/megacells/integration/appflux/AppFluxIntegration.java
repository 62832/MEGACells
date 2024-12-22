package gripe._90.megacells.integration.appflux;

import com.glodblock.github.appflux.common.AFSingletons;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public class AppFluxIntegration implements IntegrationHelper {
    @Override
    public void initUpgrades() {
        // spotless:off
        Upgrades.add(AFSingletons.INDUCTION_CARD, MEGABlocks.MEGA_INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(AFSingletons.INDUCTION_CARD, MEGAItems.MEGA_INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(AFSingletons.INDUCTION_CARD, MEGABlocks.MEGA_PATTERN_PROVIDER, 1, "group.pattern_provider.name");
        Upgrades.add(AFSingletons.INDUCTION_CARD, MEGAItems.MEGA_PATTERN_PROVIDER, 1, "group.pattern_provider.name");
        // spotless:on
    }
}

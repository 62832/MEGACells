package gripe._90.megacells.integration.appflux;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public class AppFluxIntegration implements IntegrationHelper {
    @Override
    public void initUpgrades() {
        var inductionCard =
                BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("appflux", "induction_card"));
        // spotless:off
        Upgrades.add(inductionCard, MEGABlocks.MEGA_INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(inductionCard, MEGAItems.MEGA_INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(inductionCard, MEGABlocks.MEGA_PATTERN_PROVIDER, 1, "group.pattern_provider.name");
        Upgrades.add(inductionCard, MEGAItems.MEGA_PATTERN_PROVIDER, 1, "group.pattern_provider.name");
        // spotless:on
    }
}

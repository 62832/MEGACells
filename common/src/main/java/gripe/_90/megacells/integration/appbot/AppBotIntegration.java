package gripe._90.megacells.integration.appbot;

import static gripe._90.megacells.definition.MEGAItems.GREATER_ENERGY_CARD;

import java.util.List;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import appbot.ABItems;

public final class AppBotIntegration {
    public static void initUpgrades() {
        AppBotItems.getPortables()
                .forEach(c -> Upgrades.add(GREATER_ENERGY_CARD, c, 2, GuiText.PortableCells.getTranslationKey()));

        for (var portable : List.of(
                ABItems.PORTABLE_MANA_CELL_1K,
                ABItems.PORTABLE_MANA_CELL_4K,
                ABItems.PORTABLE_MANA_CELL_16K,
                ABItems.PORTABLE_MANA_CELL_64K,
                ABItems.PORTABLE_MANA_CELL_256K)) {
            Upgrades.add(GREATER_ENERGY_CARD, portable, 2, GuiText.PortableCells.getTranslationKey());
        }
    }
}

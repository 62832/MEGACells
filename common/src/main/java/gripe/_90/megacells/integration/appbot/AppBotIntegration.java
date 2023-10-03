package gripe._90.megacells.integration.appbot;

import static gripe._90.megacells.definition.MEGAItems.GREATER_ENERGY_CARD;

import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import appbot.AppliedBotanics;

public final class AppBotIntegration {
    public static void initUpgrades() {
        AppBotItems.getPortables()
                .forEach(c -> Upgrades.add(GREATER_ENERGY_CARD, c, 2, GuiText.PortableCells.getTranslationKey()));

        for (var portable : List.of(
                cell("portable_mana_storage_cell_1k"),
                cell("portable_mana_storage_cell_4k"),
                cell("portable_mana_storage_cell_16k"),
                cell("portable_mana_storage_cell_64k"),
                cell("portable_mana_storage_cell_256k"))) {
            Upgrades.add(GREATER_ENERGY_CARD, portable, 2, GuiText.PortableCells.getTranslationKey());
        }
    }

    private static Item cell(String name) {
        return Registry.ITEM.get(AppliedBotanics.id(name));
    }
}

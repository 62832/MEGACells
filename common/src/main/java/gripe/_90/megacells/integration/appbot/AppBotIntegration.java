package gripe._90.megacells.integration.appbot;

import static gripe._90.megacells.definition.MEGAItems.GREATER_ENERGY_CARD;

import java.util.List;

import net.minecraft.world.item.Item;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;

import gripe._90.megacells.MEGACells;

public final class AppBotIntegration {
    private static final Class<?> APPBOT_ITEMS;

    static {
        try {
            APPBOT_ITEMS = Class.forName("appbot.%s.ABItems"
                    .formatted(
                            switch (MEGACells.PLATFORM.getLoader()) {
                                case FABRIC -> "fabric";
                                case FORGE -> "forge";
                            }));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initUpgrades() {
        AppBotItems.getPortables()
                .forEach(c -> Upgrades.add(GREATER_ENERGY_CARD, c, 2, GuiText.PortableCells.getTranslationKey()));

        for (var portable : List.of(
                cell("PORTABLE_MANA_CELL_1K"),
                cell("PORTABLE_MANA_CELL_4K"),
                cell("PORTABLE_MANA_CELL_16K"),
                cell("PORTABLE_MANA_CELL_64K"),
                cell("PORTABLE_MANA_CELL_256K"))) {
            Upgrades.add(GREATER_ENERGY_CARD, portable, 2, GuiText.PortableCells.getTranslationKey());
        }
    }

    private static Item cell(String name) {
        try {
            return (Item) APPBOT_ITEMS.getDeclaredField(name).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

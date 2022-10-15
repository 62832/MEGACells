package gripe._90.megacells.integration.appbot;

import java.util.Collections;
import java.util.List;

import vazkii.botania.common.lib.ModTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appbot.ABMenus;
import appbot.ae2.ManaKeyType;

import appeng.api.stacks.AEKeyType;
import appeng.menu.me.common.MEStorageMenu;

import gripe._90.megacells.item.core.IMEGACellType;

public enum AppBotCellType implements IMEGACellType {
    MANA;

    @Override
    public AEKeyType keyType() {
        return ManaKeyType.TYPE;
    }

    @Override
    public int maxTypes() {
        return 1;
    }

    @Override
    public String affix() {
        return "mana";
    }

    @Override
    public Item housing() {
        return AppBotItems.MEGA_MANA_CELL_HOUSING.asItem();
    }

    @Override
    public TagKey<Item> housingMaterial() {
        return ModTags.Items.INGOTS_TERRASTEEL;
    }

    @Override
    public MenuType<MEStorageMenu> portableCellMenu() {
        return ABMenus.PORTABLE_MANA_CELL_TYPE;
    }

    public List<Item> getCells() {
        return AppBotIntegration.isAppBotLoaded() ? List.of(AppBotItems.MANA_CELL_1M.asItem(),
                AppBotItems.MANA_CELL_4M.asItem(), AppBotItems.MANA_CELL_16M.asItem(),
                AppBotItems.MANA_CELL_64M.asItem(), AppBotItems.MANA_CELL_256M.asItem()) : Collections.emptyList();
    }

    public List<Item> getPortableCells() {
        return AppBotIntegration.isAppBotLoaded() ? List.of(AppBotItems.PORTABLE_MANA_CELL_1M.asItem(),
                AppBotItems.PORTABLE_MANA_CELL_4M.asItem(), AppBotItems.PORTABLE_MANA_CELL_16M.asItem(),
                AppBotItems.PORTABLE_MANA_CELL_64M.asItem(), AppBotItems.PORTABLE_MANA_CELL_256M.asItem())
                : Collections.emptyList();
    }
}

package gripe._90.megacells.integration.appmek.item.cell;

import java.util.Collections;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.menu.me.common.MEStorageMenu;

import me.ramidzkh.mekae2.AMMenus;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;

import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.item.AppMekItems;
import gripe._90.megacells.item.cell.IMEGACellType;

public enum AppMekCellType implements IMEGACellType {
    CHEMICAL;

    @Override
    public AEKeyType keyType() {
        return MekanismKeyType.TYPE;
    }

    @Override
    public int maxTypes() {
        return 9;
    }

    @Override
    public String affix() {
        return "Chemical";
    }

    @Override
    public ItemDefinition<MaterialItem> housing() {
        return AppMekItems.MEGA_CHEMICAL_CELL_HOUSING;
    }

    @Override
    public TagKey<Item> housingMaterial() {
        return ItemTags.create(new ResourceLocation("forge", "ingots/osmium"));
    }

    @Override
    public MenuType<MEStorageMenu> portableCellMenu() {
        return AMMenus.PORTABLE_CHEMICAL_CELL_TYPE;
    }

    public List<ItemDefinition<?>> getCells() {
        return AppMekIntegration.isAppMekLoaded() ? List.of(
                AppMekItems.CHEMICAL_CELL_1M, AppMekItems.CHEMICAL_CELL_4M,
                AppMekItems.CHEMICAL_CELL_16M, AppMekItems.CHEMICAL_CELL_64M,
                AppMekItems.CHEMICAL_CELL_256M) : Collections.emptyList();
    }

    public List<ItemDefinition<?>> getPortableCells() {
        return AppMekIntegration.isAppMekLoaded() ? List.of(
                AppMekItems.PORTABLE_CHEMICAL_CELL_1M, AppMekItems.PORTABLE_CHEMICAL_CELL_4M,
                AppMekItems.PORTABLE_CHEMICAL_CELL_16M, AppMekItems.PORTABLE_CHEMICAL_CELL_64M,
                AppMekItems.PORTABLE_CHEMICAL_CELL_256M) : Collections.emptyList();
    }
}

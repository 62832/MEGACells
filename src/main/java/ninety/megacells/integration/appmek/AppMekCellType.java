package ninety.megacells.integration.appmek;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.menu.me.common.MEStorageMenu;

import me.ramidzkh.mekae2.AMMenus;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;

import ninety.megacells.item.IMEGACellType;

public enum AppMekCellType implements IMEGACellType {
    CHEMICAL;

    @Override
    public AEKeyType keyType() {
        return MekanismKeyType.TYPE;
    }

    @Override
    public String affix() {
        return "chemical";
    }

    @Override
    public Item housing() {
        return AppMekItems.MEGA_CHEMICAL_CELL_HOUSING.asItem();
    }

    @Override
    public TagKey<Item> housingMaterial() {
        return ItemTags.create(new ResourceLocation("forge", "ingots/osmium"));
    }

    @Override
    public MenuType<MEStorageMenu> portableCellMenu() {
        return AMMenus.PORTABLE_CHEMICAL_CELL_TYPE;
    }

    public List<Item> getCells() {
        return List.of(AppMekItems.CHEMICAL_CELL_1M.asItem(), AppMekItems.CHEMICAL_CELL_4M.asItem(),
                AppMekItems.CHEMICAL_CELL_16M.asItem(), AppMekItems.CHEMICAL_CELL_64M.asItem(),
                AppMekItems.CHEMICAL_CELL_256M.asItem());
    }

    public List<Item> getPortableCells() {
        return List.of(AppMekItems.PORTABLE_CHEMICAL_CELL_1M.asItem(), AppMekItems.PORTABLE_CHEMICAL_CELL_4M.asItem(),
                AppMekItems.PORTABLE_CHEMICAL_CELL_16M.asItem(), AppMekItems.PORTABLE_CHEMICAL_CELL_64M.asItem(),
                AppMekItems.PORTABLE_CHEMICAL_CELL_256M.asItem());
    }
}

package ninety.megacells.integration.appmek;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import me.ramidzkh.mekae2.AMMenus;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import ninety.megacells.item.util.IMEGACellType;

import appeng.api.stacks.AEKeyType;
import appeng.menu.me.common.MEStorageMenu;

public enum ChemicalCellType implements IMEGACellType {
    TYPE;

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
        return MEGAMekItems.MEGA_CHEMICAL_CELL_HOUSING;
    }

    @Override
    public TagKey<Item> housingMaterial() {
        return ItemTags.create(new ResourceLocation("forge", "ingots/osmium"));
    }

    @Override
    public MenuType<MEStorageMenu> portableCellMenu() {
        return AMMenus.PORTABLE_CHEMICAL_CELL_TYPE;
    }

    @Override
    public List<Item> getCells() {
        var cells = new ArrayList<Item>();
        cells.add(MEGAMekItems.CHEMICAL_STORAGE_CELL_1M);
        cells.add(MEGAMekItems.CHEMICAL_STORAGE_CELL_4M);
        cells.add(MEGAMekItems.CHEMICAL_STORAGE_CELL_16M);
        cells.add(MEGAMekItems.CHEMICAL_STORAGE_CELL_64M);
        cells.add(MEGAMekItems.CHEMICAL_STORAGE_CELL_256M);
        return cells;
    }

    @Override
    public List<Item> getPortableCells() {
        var cells = new ArrayList<Item>();
        cells.add(MEGAMekItems.PORTABLE_CHEMICAL_CELL_1M);
        cells.add(MEGAMekItems.PORTABLE_CHEMICAL_CELL_4M);
        cells.add(MEGAMekItems.PORTABLE_CHEMICAL_CELL_16M);
        cells.add(MEGAMekItems.PORTABLE_CHEMICAL_CELL_64M);
        cells.add(MEGAMekItems.PORTABLE_CHEMICAL_CELL_256M);
        return cells;
    }
}

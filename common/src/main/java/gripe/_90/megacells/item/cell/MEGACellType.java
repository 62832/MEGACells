package gripe._90.megacells.item.cell;

import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.menu.me.common.MEStorageMenu;

import gripe._90.megacells.item.MEGAItems;

public enum MEGACellType implements IMEGACellType {
    ITEM(AEKeyType.items(), 63, "item"),
    FLUID(AEKeyType.fluids(), 9, "fluid");

    private final AEKeyType key;
    private final int types;
    private final String affix;

    MEGACellType(AEKeyType key, int types, String affix) {
        this.key = key;
        this.types = types;
        this.affix = affix;
    }

    @Override
    public AEKeyType keyType() {
        return this.key;
    }

    @Override
    public int maxTypes() {
        return this.types;
    }

    @Override
    public String affix() {
        return this.affix;
    }

    public Item housing() {
        return switch (this) {
            case ITEM -> MEGAItems.MEGA_ITEM_CELL_HOUSING.asItem();
            case FLUID -> MEGAItems.MEGA_FLUID_CELL_HOUSING.asItem();
        };
    }

    @Override
    public TagKey<Item> housingMaterial() {
        return switch (this) {
            case ITEM -> ConventionTags.IRON_INGOT;
            case FLUID -> ConventionTags.COPPER_INGOT;
        };
    }

    @Override
    public MenuType<MEStorageMenu> portableCellMenu() {
        return switch (this) {
            case ITEM -> MEStorageMenu.PORTABLE_ITEM_CELL_TYPE;
            case FLUID -> MEStorageMenu.PORTABLE_FLUID_CELL_TYPE;
        };
    }

    public List<Item> getCells() {
        return switch (this) {
            case ITEM -> List.of(
                    MEGAItems.ITEM_CELL_1M.asItem(),
                    MEGAItems.ITEM_CELL_4M.asItem(),
                    MEGAItems.ITEM_CELL_16M.asItem(),
                    MEGAItems.ITEM_CELL_64M.asItem(),
                    MEGAItems.ITEM_CELL_256M.asItem());
            case FLUID -> List.of(
                    MEGAItems.FLUID_CELL_1M.asItem(),
                    MEGAItems.FLUID_CELL_4M.asItem(),
                    MEGAItems.FLUID_CELL_16M.asItem(),
                    MEGAItems.FLUID_CELL_64M.asItem(),
                    MEGAItems.FLUID_CELL_256M.asItem());
        };
    }

    public List<Item> getPortableCells() {
        return switch (this) {
            case ITEM -> List.of(
                    MEGAItems.PORTABLE_ITEM_CELL_1M.asItem(),
                    MEGAItems.PORTABLE_ITEM_CELL_4M.asItem(),
                    MEGAItems.PORTABLE_ITEM_CELL_16M.asItem(),
                    MEGAItems.PORTABLE_ITEM_CELL_64M.asItem(),
                    MEGAItems.PORTABLE_ITEM_CELL_256M.asItem());
            case FLUID -> List.of(
                    MEGAItems.PORTABLE_FLUID_CELL_1M.asItem(),
                    MEGAItems.PORTABLE_FLUID_CELL_4M.asItem(),
                    MEGAItems.PORTABLE_FLUID_CELL_16M.asItem(),
                    MEGAItems.PORTABLE_FLUID_CELL_64M.asItem(),
                    MEGAItems.PORTABLE_FLUID_CELL_256M.asItem());
        };
    }
}

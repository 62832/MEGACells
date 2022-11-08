package gripe._90.megacells.item.cell;

import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.items.materials.MaterialItem;
import appeng.menu.me.common.MEStorageMenu;

import gripe._90.megacells.definition.MEGAItems;

public enum MEGACellType implements IMEGACellType {
    ITEM(AEKeyType.items(), 63, "Item"),
    FLUID(AEKeyType.fluids(), 9, "Fluid");

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

    public ItemDefinition<MaterialItem> housing() {
        return switch (this) {
            case ITEM -> MEGAItems.MEGA_ITEM_CELL_HOUSING;
            case FLUID -> MEGAItems.MEGA_FLUID_CELL_HOUSING;
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

    public List<ItemDefinition<?>> getCells() {
        return switch (this) {
            case ITEM -> List.of(
                    MEGAItems.ITEM_CELL_1M,
                    MEGAItems.ITEM_CELL_4M,
                    MEGAItems.ITEM_CELL_16M,
                    MEGAItems.ITEM_CELL_64M,
                    MEGAItems.ITEM_CELL_256M);
            case FLUID -> List.of(
                    MEGAItems.FLUID_CELL_1M,
                    MEGAItems.FLUID_CELL_4M,
                    MEGAItems.FLUID_CELL_16M,
                    MEGAItems.FLUID_CELL_64M,
                    MEGAItems.FLUID_CELL_256M);
        };
    }

    public List<ItemDefinition<?>> getPortableCells() {
        return switch (this) {
            case ITEM -> List.of(
                    MEGAItems.PORTABLE_ITEM_CELL_1M,
                    MEGAItems.PORTABLE_ITEM_CELL_4M,
                    MEGAItems.PORTABLE_ITEM_CELL_16M,
                    MEGAItems.PORTABLE_ITEM_CELL_64M,
                    MEGAItems.PORTABLE_ITEM_CELL_256M);
            case FLUID -> List.of(
                    MEGAItems.PORTABLE_FLUID_CELL_1M,
                    MEGAItems.PORTABLE_FLUID_CELL_4M,
                    MEGAItems.PORTABLE_FLUID_CELL_16M,
                    MEGAItems.PORTABLE_FLUID_CELL_64M,
                    MEGAItems.PORTABLE_FLUID_CELL_256M);
        };
    }
}

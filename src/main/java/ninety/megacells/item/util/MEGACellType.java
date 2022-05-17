package ninety.megacells.item.util;

import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.menu.me.common.MEStorageMenu;

import ninety.megacells.item.MEGAItems;

public enum MEGACellType implements IMEGACellType {
    ITEM(AEKeyType.items(), "item", ConventionTags.IRON_INGOT, MEStorageMenu.PORTABLE_ITEM_CELL_TYPE),
    FLUID(AEKeyType.fluids(), "fluid", ConventionTags.COPPER_INGOT, MEStorageMenu.PORTABLE_FLUID_CELL_TYPE),
    ;

    private final AEKeyType key;
    private final String affix;
    private final TagKey<Item> housingMaterial;
    private final MenuType<MEStorageMenu> portableMenu;

    MEGACellType(AEKeyType key, String affix, TagKey<Item> housingMaterial, MenuType<MEStorageMenu> portableMenu) {
        this.key = key;
        this.affix = affix;
        this.housingMaterial = housingMaterial;
        this.portableMenu = portableMenu;
    }

    @Override
    public AEKeyType keyType() {
        return this.key;
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
        return this.housingMaterial;
    }

    @Override
    public MenuType<MEStorageMenu> portableCellMenu() {
        return this.portableMenu;
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

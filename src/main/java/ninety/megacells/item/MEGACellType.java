package ninety.megacells.item;

import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.menu.me.common.MEStorageMenu;

public enum MEGACellType {
    ITEM(AEKeyType.items(), "item", ConventionTags.IRON_INGOT, MEStorageMenu.PORTABLE_ITEM_CELL_TYPE),
    FLUID(AEKeyType.fluids(), "fluid", ConventionTags.COPPER_INGOT, MEStorageMenu.PORTABLE_FLUID_CELL_TYPE),
    // CHEM(MekanismKeyType.TYPE, "chemical", AMItems.CHEMICAL_CELL_HOUSING.get(), AMMenus.PORTABLE_CHEMICAL_CELL_TYPE)
    ;

    public final AEKeyType key;
    public final String affix;
    public final TagKey<Item> housingMaterial;
    public final MenuType<MEStorageMenu> portableMenu;

    MEGACellType(AEKeyType key, String affix, TagKey<Item> housingMaterial, MenuType<MEStorageMenu> portableMenu) {
        this.key = key;
        this.affix = affix;
        this.housingMaterial = housingMaterial;
        this.portableMenu = portableMenu;
    }

    public Item getHousing() {
        return switch (this) {
            case ITEM -> MEGAItems.MEGA_ITEM_CELL_HOUSING.get();
            case FLUID -> MEGAItems.MEGA_FLUID_CELL_HOUSING.get();
        };
    }

    public List<Item> getCells() {
        return switch (this) {
            case ITEM -> List.of(MEGAItems.ITEM_CELL_1M.get(), MEGAItems.ITEM_CELL_4M.get(),
                    MEGAItems.ITEM_CELL_16M.get(), MEGAItems.ITEM_CELL_64M.get(), MEGAItems.ITEM_CELL_256M.get());
            case FLUID -> List.of(MEGAItems.FLUID_CELL_1M.get(), MEGAItems.FLUID_CELL_4M.get(),
                    MEGAItems.FLUID_CELL_16M.get(), MEGAItems.FLUID_CELL_64M.get(), MEGAItems.FLUID_CELL_256M.get());
        };
    }

    public List<Item> getPortableCells() {
        return switch (this) {
            case ITEM -> List.of(MEGAItems.PORTABLE_ITEM_CELL_1M.get(), MEGAItems.PORTABLE_ITEM_CELL_4M.get(),
                    MEGAItems.PORTABLE_ITEM_CELL_16M.get(), MEGAItems.PORTABLE_ITEM_CELL_64M.get(),
                    MEGAItems.PORTABLE_ITEM_CELL_256M.get());
            case FLUID -> List.of(MEGAItems.PORTABLE_FLUID_CELL_1M.get(), MEGAItems.PORTABLE_FLUID_CELL_4M.get(),
                    MEGAItems.PORTABLE_FLUID_CELL_16M.get(), MEGAItems.PORTABLE_FLUID_CELL_64M.get(),
                    MEGAItems.PORTABLE_FLUID_CELL_256M.get());
        };
    }
}

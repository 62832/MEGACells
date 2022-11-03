package gripe._90.megacells.item.cell;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.menu.me.common.MEStorageMenu;

public interface IMEGACellType {
    AEKeyType keyType();

    int maxTypes();

    String affix();

    ItemDefinition<MaterialItem> housing();

    TagKey<Item> housingMaterial();

    MenuType<MEStorageMenu> portableCellMenu();
}

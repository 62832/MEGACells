package gripe._90.megacells.item.core;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.menu.me.common.MEStorageMenu;

public interface IMEGACellType {
    AEKeyType keyType();

    int maxTypes();

    String affix();

    Item housing();

    TagKey<Item> housingMaterial();

    MenuType<MEStorageMenu> portableCellMenu();
}

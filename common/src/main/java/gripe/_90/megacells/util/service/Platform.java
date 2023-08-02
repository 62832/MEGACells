package gripe._90.megacells.util.service;

import net.minecraft.world.item.CreativeModeTab;

import gripe._90.megacells.util.Addons;
import gripe._90.megacells.util.Loaders;

public interface Platform {
    Loaders getLoader();

    CreativeModeTab getCreativeTab(CreativeModeTab.DisplayItemsGenerator display);

    boolean isAddonLoaded(Addons addon);
}

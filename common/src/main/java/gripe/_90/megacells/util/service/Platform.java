package gripe._90.megacells.util.service;

import net.minecraft.world.item.CreativeModeTab;

import gripe._90.megacells.util.Addons;

public interface Platform {
    Loader getLoader();

    CreativeModeTab getCreativeTab(CreativeModeTab.DisplayItemsGenerator display);

    boolean isAddonLoaded(Addons modId);

    enum Loader {
        FABRIC, FORGE
    }
}

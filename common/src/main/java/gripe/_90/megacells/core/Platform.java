package gripe._90.megacells.core;

import net.minecraft.world.item.CreativeModeTab;

public interface Platform {
    Loaders getLoader();

    CreativeModeTab getCreativeTab();

    boolean isAddonLoaded(Addons addon);
}

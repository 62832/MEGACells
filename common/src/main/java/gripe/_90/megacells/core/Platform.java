package gripe._90.megacells.core;

import net.minecraft.world.item.CreativeModeTab;

public interface Platform {
    CreativeModeTab getCreativeTab();

    boolean isAddonLoaded(Addons addon);
}

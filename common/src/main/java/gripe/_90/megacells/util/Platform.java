package gripe._90.megacells.util;

import net.minecraft.world.item.CreativeModeTab;

public interface Platform {
    Loaders getLoader();

    CreativeModeTab.Builder getCreativeTabBuilder();

    boolean isAddonLoaded(Addons addon);

    void initCompression();
}

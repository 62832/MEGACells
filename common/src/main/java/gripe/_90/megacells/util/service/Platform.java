package gripe._90.megacells.util.service;

import net.minecraft.world.item.CreativeModeTab;

public interface Platform {
    CreativeModeTab getCreativeTab(CreativeModeTab.DisplayItemsGenerator display);

    boolean isModLoaded(String modId);
}

package gripe._90.megacells.util.service;

import net.minecraft.world.item.CreativeModeTab;

public interface Platform {
    CreativeModeTab getCreativeTab();

    boolean isModLoaded(String modId);
}

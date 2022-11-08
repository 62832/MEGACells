package gripe._90.megacells;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import gripe._90.megacells.util.Services;

public final class MEGACells {
    public static final String MODID = "megacells";

    public static final CreativeModeTab CREATIVE_TAB = Services.PLATFORM.getCreativeTab();

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}

package gripe._90.megacells;

import java.util.Objects;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import gripe._90.megacells.platform.Services;

public final class MEGACells {
    public static final String MODID = "megacells";

    public static CreativeModeTab CREATIVE_TAB = Services.PLATFORM.getCreativeTab();

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static String getItemPath(Item item) {
        return Objects.requireNonNull(Registry.ITEM.getKey(item)).getPath();
    }
}

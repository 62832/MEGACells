package gripe._90.megacells.util;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

import gripe._90.megacells.util.service.Platform;

public final class Utils {
    private Utils() {
    }

    public static final String MODID = "megacells";

    public static final Logger LOGGER = LoggerFactory.getLogger("MEGA Cells");

    public static final Platform PLATFORM = ServiceLoader.load(Platform.class).findFirst().orElseThrow();

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}

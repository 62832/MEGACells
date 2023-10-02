package gripe._90.megacells;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

import gripe._90.megacells.core.Platform;

public final class MEGACells {
    private MEGACells() {}

    public static final String MODID = "megacells";

    public static final Logger LOGGER = LoggerFactory.getLogger("MEGA Cells");

    public static final Platform PLATFORM =
            ServiceLoader.load(Platform.class).findFirst().orElseThrow();

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}

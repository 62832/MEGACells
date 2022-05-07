package ninety.megacells.util;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import ninety.megacells.MEGACells;

public class MEGACellsUtil {
    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MEGACells.MODID, path);
    }

    public static String getItemPath(Item item) {
        return Objects.requireNonNull(item.getRegistryName()).getPath();
    }
}

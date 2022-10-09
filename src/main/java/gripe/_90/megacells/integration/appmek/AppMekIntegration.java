package gripe._90.megacells.integration.appmek;

import net.minecraftforge.fml.ModList;

public final class AppMekIntegration {
    public static boolean isAppMekLoaded() {
        return ModList.get().isLoaded("appmek");
    }
}

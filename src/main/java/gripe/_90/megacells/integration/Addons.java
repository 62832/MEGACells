package gripe._90.megacells.integration;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public enum Addons {
    APPMEK("appmek"),
    APPBOT("appbot"),
    ARSENG("arseng"),
    AE2WTLIB("ae2wtlib");

    private final String modId;

    Addons(String modId) {
        this.modId = modId;
    }

    public String getModId() {
        return modId;
    }

    public static boolean isLoaded(Addons addon) {
        return ModList.get() != null
                ? ModList.get().isLoaded(addon.getModId())
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(addon.getModId()::equals);
    }
}

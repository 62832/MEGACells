package gripe._90.megacells.integration.forge.ae2wt;

import net.minecraftforge.fml.ModList;

import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;

public final class AE2WTIntegrationForge {
    public static boolean isAE2WTLoaded() {
        return ModList.get().isLoaded("ae2wtlib");
    }

    public static void initIntegration() {
        if (isAE2WTLoaded()) {
            AE2WTIntegration.initIntegration();
        }
    }
}

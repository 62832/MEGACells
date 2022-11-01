package gripe._90.megacells.integration.ae2wt;

import gripe._90.megacells.platform.Services;

public final class AE2WTIntegrationForge {
    public static void initIntegration() {
        if (Services.PLATFORM.isModLoaded("ae2wtlib")) {
            AE2WTIntegration.initIntegration();
        }
    }
}

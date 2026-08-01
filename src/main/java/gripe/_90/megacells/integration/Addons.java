package gripe._90.megacells.integration;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;
import gripe._90.megacells.integration.appflux.AppFluxIntegration;
import gripe._90.megacells.integration.appmek.AppMekIntegration;

@SuppressWarnings("Convert2MethodRef")
public enum Addons {
    AE2WTLIB("AE2WTLib", () -> new AE2WTIntegration(), true),
    APPMEK("Applied Mekanistics", () -> new AppMekIntegration(), true),
    APPBOT("Applied Botanics", () -> new AppBotIntegration(), true),
    ARSENG("Ars Énergistique"),
    APPEX("Applied Experienced"),
    APPFLUX("Applied Flux", () -> new AppFluxIntegration(), true),
    // AppliedE stays disabled even as a compile-only stub: MEGA's own MEGAEMCInterfaceBlockEntity/
    // MEGAEMCInterfacePart directly `extends` AppliedE's own classes (not just reference them), so the
    // JVM eagerly resolves that superclass the moment MEGA's own classes are loaded - crashing the
    // WHOLE mod's construction, not just this integration, regardless of Addons#isLoaded gating.
    // Restore only once AppliedE ships a real 26.1 build to be present at runtime too.
    APPLIEDE("AppliedE"),
    APPLIEDSOUL("Applied Soul"),
// APPELEM("Applied Elemental"),
;

    private final String modName;
    private final Supplier<IntegrationHelper> helper;
    private final boolean enabled; // TODO: Needs a more dynamic approach than this...

    Addons(String modName) {
        this(modName, () -> new IntegrationHelper() {}, false);
    }

    Addons(String modName, Supplier<IntegrationHelper> helper, boolean enabled) {
        this.modName = modName;
        this.helper = Lazy.of(helper);
        this.enabled = enabled;
    }

    public String getModId() {
        return name().toLowerCase();
    }

    public IntegrationHelper getHelper() {
        return helper.get();
    }

    public boolean isLoaded() {
        return enabled && ModList.get().isLoaded(getModId());
    }

    public Component getUnavailableTooltip() {
        return (enabled ? MEGATranslations.NotInstalled.text(modName) : MEGATranslations.NotYetAvailable.text())
                .withStyle(ChatFormatting.GRAY);
    }
}

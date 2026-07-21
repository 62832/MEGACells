package gripe._90.megacells.integration;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.integration.appflux.AppFluxIntegration;

@SuppressWarnings("Convert2MethodRef")
public enum Addons {
    AE2WTLIB("AE2WTLib"),
    APPMEK("Applied Mekanistics"),
    APPBOT("Applied Botanics"),
    ARSENG("Ars Énergistique"),
    APPEX("Applied Experienced"),
    APPFLUX("Applied Flux", () -> new AppFluxIntegration(), true),
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

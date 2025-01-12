package gripe._90.megacells.integration;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.util.Lazy;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;
import gripe._90.megacells.integration.appex.AppExIntegration;
import gripe._90.megacells.integration.appflux.AppFluxIntegration;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.arseng.ArsEngIntegration;

@SuppressWarnings("Convert2MethodRef")
public enum Addons {
    AE2WTLIB_API("AE2WTLib", () -> new AE2WTIntegration()),
    APPMEK("Applied Mekanistics", () -> new AppMekIntegration()),
    APPBOT("Applied Botanics", () -> new AppBotIntegration(), false),
    ARSENG("Ars Énergistique", () -> new ArsEngIntegration()),
    APPEX("Applied Experienced", () -> new AppExIntegration()),
    APPFLUX("Applied Flux", () -> new AppFluxIntegration()),
// APPLIEDE("AppliedE"),
// APPELEM("Applied Elemental"),
;

    private final String modName;
    private final Supplier<IntegrationHelper> helper;
    private final boolean enabled;

    Addons(String modName, Supplier<IntegrationHelper> helper) {
        this(modName, helper, true);
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
        return enabled
                && (ModList.get() != null
                        ? ModList.get().isLoaded(getModId())
                        : LoadingModList.get().getMods().stream()
                                .map(ModInfo::getModId)
                                .anyMatch(getModId()::equals));
    }

    public Component getUnavailableTooltip() {
        return (enabled ? MEGATranslations.NotInstalled.text(modName) : MEGATranslations.NotYetAvailable.text())
                .withStyle(ChatFormatting.GRAY);
    }
}

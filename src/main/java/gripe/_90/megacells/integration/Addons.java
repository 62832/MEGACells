package gripe._90.megacells.integration;

import java.util.function.Supplier;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.util.Lazy;

import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appex.AppExIntegration;
import gripe._90.megacells.integration.appflux.AppFluxIntegration;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.arseng.ArsEngIntegration;

@SuppressWarnings("Convert2MethodRef")
public enum Addons {
    APPMEK("Applied Mekanistics", () -> new AppMekIntegration()),
    ARSENG("Ars Énergistique", () -> new ArsEngIntegration()),
    AE2WTLIB_API("AE2WTLib", () -> new AE2WTIntegration()),
    APPFLUX("Applied Flux", () -> new AppFluxIntegration()),
    APPEX("Applied Experienced", () -> new AppExIntegration()),
// APPBOT("Applied Botanics"),
// APPLIEDE("AppliedE"),
// APPELEM("Applied Elemental"),
;

    private final String modName;
    private final Supplier<IntegrationHelper> helper;

    Addons(String modName, Supplier<IntegrationHelper> helper) {
        this.modName = modName;
        this.helper = Lazy.of(helper);
    }

    public String getModId() {
        return name().toLowerCase();
    }

    public String getModName() {
        return modName;
    }

    public IntegrationHelper getHelper() {
        return helper.get();
    }

    public boolean isLoaded() {
        return ModList.get() != null
                ? ModList.get().isLoaded(getModId())
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(getModId()::equals);
    }

    public RecipeOutput conditionalRecipe(RecipeOutput output) {
        return output.withConditions(new ModLoadedCondition(getModId()));
    }
}

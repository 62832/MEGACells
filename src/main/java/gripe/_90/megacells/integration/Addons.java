package gripe._90.megacells.integration;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public enum Addons {
    APPMEK("Applied Mekanistics"),
    APPBOT("Applied Botanics"),
    ARSENG("Ars Énergistique"),
    APPLIEDE("AppliedE"),
    AE2WTLIB("AE2WTLib");

    private final String modName;

    Addons(String modName) {
        this.modName = modName;
    }

    public String getModId() {
        return name().toLowerCase();
    }

    public String getModName() {
        return modName;
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

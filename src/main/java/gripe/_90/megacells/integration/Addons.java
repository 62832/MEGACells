package gripe._90.megacells.integration;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public enum Addons {
    APPMEK,
    APPBOT,
    ARSENG,
    APPLIEDE,
    AE2WTLIB;

    public String getModId() {
        return name().toLowerCase();
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

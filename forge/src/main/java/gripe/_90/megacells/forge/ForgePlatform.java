package gripe._90.megacells.forge;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.util.CompressionService;

public final class ForgePlatform implements Platform {
    private RecipeManager recipeManager;
    private RegistryAccess registryAccess;

    @Override
    public Loaders getLoader() {
        return Loaders.FORGE;
    }

    @Override
    public CreativeModeTab.Builder getCreativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public boolean isAddonLoaded(Addons addon) {
        if (ModList.get() == null) {
            // lol
            return LoadingModList.get().getMods().stream()
                    .map(ModInfo::getModId)
                    .anyMatch(addon.getModId()::equals);
        }

        return ModList.get().isLoaded(addon.getModId());
    }

    @Override
    public void initCompression() {
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            recipeManager = event.getServer().getRecipeManager();
            registryAccess = event.getServer().registryAccess();

            CompressionService.INSTANCE.loadRecipes(recipeManager, registryAccess);
        });

        // Because RecipesUpdatedEvent is a client-side event for whatever reason...
        MinecraftForge.EVENT_BUS.addListener((TagsUpdatedEvent event) -> {
            if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD
                    && recipeManager != null
                    && registryAccess != null) {
                CompressionService.INSTANCE.loadRecipes(recipeManager, registryAccess);
            }
        });
    }
}

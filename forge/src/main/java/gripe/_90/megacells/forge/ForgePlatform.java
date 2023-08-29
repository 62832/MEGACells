package gripe._90.megacells.forge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.service.CompressionService;

public final class ForgePlatform implements Platform {
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
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> CompressionService.loadRecipes(
                event.getServer().getRecipeManager(), event.getServer().registryAccess()));
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> CompressionService.loadRecipes(
                event.getServerResources().getRecipeManager(), event.getRegistryAccess()));
    }
}

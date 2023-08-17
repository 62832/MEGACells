package gripe._90.megacells.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.CreativeModeTab;

import gripe._90.megacells.service.CompressionService;
import gripe._90.megacells.util.Addons;
import gripe._90.megacells.util.Loaders;
import gripe._90.megacells.util.Platform;

public final class FabricPlatform implements Platform {
    @Override
    public Loaders getLoader() {
        return Loaders.FABRIC;
    }

    @Override
    public CreativeModeTab.Builder getCreativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public boolean isAddonLoaded(Addons addon) {
        return FabricLoader.getInstance().isModLoaded(addon.getModId());
    }

    @Override
    public void initCompression() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess()));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
            }
        });
    }
}

package gripe._90.megacells.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import appeng.init.InitVillager;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.util.CompressionService;

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
            if (success) CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
        });
    }

    @Override
    public void addVillagerTrade(ItemLike item, int cost, int quantity, int xp) {
        TradeOfferHelper.registerVillagerOffers(
                InitVillager.PROFESSION,
                5,
                builder -> builder.add(new VillagerTrades.ItemsForEmeralds(item.asItem(), cost, quantity, xp)));
    }
}

package gripe._90.megacells.fabric;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.orientation.BlockOrientation;
import appeng.client.render.BakedModelUnwrapper;
import appeng.client.render.model.DriveBakedModel;
import appeng.core.definitions.AEBlocks;
import appeng.init.InitVillager;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.misc.CompressionService;

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

    @Override
    public BakedModel createWrappedCellModel(Item cell, BlockOrientation orientation) {
        var driveModel = Minecraft.getInstance()
                .getModelManager()
                .getBlockModelShaper()
                .getBlockModel(AEBlocks.DRIVE.block().defaultBlockState());
        var cellModel =
                BakedModelUnwrapper.unwrap(driveModel, DriveBakedModel.class).getCellChassisModel(cell);
        return new WrappedCellModel(cellModel, orientation);
    }

    private static class WrappedCellModel extends ForwardingBakedModel {
        private final BlockOrientation r;

        private WrappedCellModel(BakedModel base, BlockOrientation r) {
            wrapped = base;
            this.r = r;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
            if (side != null) {
                side = r.resultingRotate(side); // This fixes the incorrect lightmap position
            }
            List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand));

            for (int i = 0; i < quads.size(); i++) {
                BakedQuad quad = quads.get(i);
                quads.set(
                        i,
                        new BakedQuad(
                                quad.getVertices(),
                                quad.getTintIndex(),
                                r.rotate(quad.getDirection()),
                                quad.getSprite(),
                                quad.isShade()));
            }

            return quads;
        }
    }
}

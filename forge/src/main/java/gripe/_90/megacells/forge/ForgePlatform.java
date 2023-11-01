package gripe._90.megacells.forge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import appeng.api.orientation.BlockOrientation;
import appeng.client.render.BakedModelUnwrapper;
import appeng.client.render.DelegateBakedModel;
import appeng.client.render.model.DriveBakedModel;
import appeng.core.definitions.AEBlocks;
import appeng.init.InitVillager;

import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.misc.CompressionService;

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
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            var server = event.getServer();
            CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
        });

        MinecraftForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
            // Only rebuild cache in the event of a data pack /reload and not when a new player joins
            if (event.getPlayer() == null) {
                var server = event.getPlayerList().getServer();
                CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
            }
        });
    }

    @Override
    public void addVillagerTrade(ItemLike item, int cost, int quantity, int xp) {
        var offers = VillagerTrades.TRADES.computeIfAbsent(InitVillager.PROFESSION, k -> new Int2ObjectOpenHashMap<>());
        var masterEntries = offers.computeIfAbsent(5, k -> new VillagerTrades.ItemListing[0]);
        masterEntries = ArrayUtils.add(
                masterEntries,
                (i, j) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, cost), new ItemStack(item, quantity), 12, xp, 0.05F));
        offers.put(5, masterEntries);
    }

    @Override
    public BakedModel createWrappedCellModel(Item cell, BlockOrientation orientation) {
        var driveModel = Minecraft.getInstance()
                .getModelManager()
                .getBlockModelShaper()
                .getBlockModel(AEBlocks.DRIVE.block().defaultBlockState());
        var cellModel =
                BakedModelUnwrapper.unwrap(driveModel, DriveBakedModel.class).getCellChassisModel(cell);

        return new DelegateBakedModel(cellModel) {
            @NotNull
            @Override
            public List<BakedQuad> getQuads(
                    @Nullable BlockState state,
                    @Nullable Direction side,
                    @NotNull RandomSource rand,
                    @NotNull ModelData extraData,
                    RenderType renderType) {
                if (side != null) {
                    side = orientation.resultingRotate(side); // This fixes the incorrect lightmap position
                }
                List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));

                for (int i = 0; i < quads.size(); i++) {
                    BakedQuad quad = quads.get(i);
                    quads.set(
                            i,
                            new BakedQuad(
                                    quad.getVertices(),
                                    quad.getTintIndex(),
                                    orientation.rotate(quad.getDirection()),
                                    quad.getSprite(),
                                    quad.isShade()));
                }

                return quads;
            }
        };
    }
}

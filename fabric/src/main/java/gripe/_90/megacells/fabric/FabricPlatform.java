package gripe._90.megacells.fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.orientation.BlockOrientation;
import appeng.client.render.BakedModelUnwrapper;
import appeng.client.render.SimpleModelLoader;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.model.DriveBakedModel;
import appeng.core.definitions.AEBlocks;
import appeng.init.InitVillager;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Loaders;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGACreativeTab;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.misc.CompressionService;
import gripe._90.megacells.misc.LavaTransformLogic;

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
    public void register() {
        for (var block : MEGABlocks.getBlocks()) {
            Registry.register(BuiltInRegistries.BLOCK, block.id(), block.block());
            Registry.register(BuiltInRegistries.ITEM, block.id(), block.asItem());
        }

        for (var item : MEGAItems.getItems()) {
            Registry.register(BuiltInRegistries.ITEM, item.id(), item.asItem());
        }

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MEGACreativeTab.ID, MEGACreativeTab.TAB);

        for (var blockEntity : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntity.getKey(), blockEntity.getValue());
        }

        for (var menu : MEGAMenus.getMenuTypes().entrySet()) {
            Registry.register(BuiltInRegistries.MENU, menu.getKey(), menu.getValue());
        }
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
    public void initLavaTransform() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> LavaTransformLogic.clearCache());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) LavaTransformLogic.clearCache();
        });
    }

    @Override
    public void addVillagerTrade(ItemLike item, int cost, int quantity, int xp) {
        TradeOfferHelper.registerVillagerOffers(
                InitVillager.PROFESSION,
                5,
                builder -> builder.add(new VillagerTrades.ItemsForEmeralds(item.asItem(), cost, quantity, xp)));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void addIntegrationRecipe(
            Consumer<FinishedRecipe> writer, FinishedRecipe recipe, Addons addon, ResourceLocation id) {
        Consumer<FinishedRecipe> withConditions = json -> {
            FabricDataGenHelper.addConditions(
                    json, new ConditionJsonProvider[] {DefaultResourceConditions.allModsLoaded(addon.getModId())});
            writer.accept(json);
        };

        withConditions.accept(recipe);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void addIntegrationRecipe(
            Consumer<FinishedRecipe> writer, RecipeBuilder builder, Addons addon, ResourceLocation id) {
        Consumer<FinishedRecipe> withConditions = json -> {
            FabricDataGenHelper.addConditions(
                    json, new ConditionJsonProvider[] {DefaultResourceConditions.allModsLoaded(addon.getModId())});
            writer.accept(json);
        };

        builder.save(withConditions, id);
    }

    public static class Client implements Platform.Client {
        @Override
        public void initScreens() {
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> screens());
        }

        @Override
        public void initEnergyCellProps() {
            energyCellProps().run();
        }

        @Override
        public void initCraftingUnitModels() {
            for (var type : MEGACraftingUnitType.values()) {
                ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new SimpleModelLoader<>(
                        MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                        () -> new CraftingCubeModel(new MEGACraftingUnitModelProvider(type))));

                BlockRenderLayerMap.INSTANCE.putBlock(type.getDefinition().block(), RenderType.cutout());
            }
        }

        @Override
        public void initItemColours(ItemColor color, List<ItemLike> items) {
            ColorProviderRegistry.ITEM.register(color, items.toArray(new ItemLike[0]));
        }

        @Override
        public BakedModel createCellModel(Item cell, BlockOrientation orientation) {
            var driveModel = BakedModelUnwrapper.unwrap(
                    Minecraft.getInstance()
                            .getModelManager()
                            .getBlockModelShaper()
                            .getBlockModel(AEBlocks.DRIVE.block().defaultBlockState()),
                    DriveBakedModel.class);
            return driveModel == null ? null : new WrappedCellModel(driveModel.getCellChassisModel(cell), orientation);
        }
    }

    private static class WrappedCellModel extends ForwardingBakedModel {
        private final BlockOrientation orientation;

        private WrappedCellModel(BakedModel base, BlockOrientation orientation) {
            wrapped = base;
            this.orientation = orientation;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
            if (side != null) {
                side = orientation.resultingRotate(side); // This fixes the incorrect lightmap position
            }

            var quads = new ArrayList<>(super.getQuads(state, side, rand));

            for (int i = 0; i < quads.size(); i++) {
                var quad = quads.get(i);
                var baked = new BakedQuad(
                        quad.getVertices(),
                        quad.getTintIndex(),
                        orientation.rotate(quad.getDirection()),
                        quad.getSprite(),
                        quad.isShade());
                quads.set(i, baked);
            }

            return quads;
        }
    }
}

package gripe._90.megacells.forge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.api.orientation.BlockOrientation;
import appeng.client.render.BakedModelUnwrapper;
import appeng.client.render.DelegateBakedModel;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.model.DriveBakedModel;
import appeng.core.definitions.AEBlocks;
import appeng.hooks.BuiltInModelHooks;
import appeng.init.InitVillager;

import me.shedaniel.autoconfig.AutoConfig;

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
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.misc.CompressionService;
import gripe._90.megacells.misc.LavaTransformLogic;

public final class ForgePlatform implements Platform {
    private static final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

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
    public void initItems() {
        Platform.super.initItems();

        if (isAddonLoaded(Addons.APPMEK)) {
            AppMekItems.init();
        }
    }

    @Override
    public List<ItemLike> getAllCells() {
        var cells = Platform.super.getAllCells();

        if (isAddonLoaded(Addons.APPMEK)) {
            cells.addAll(AppMekItems.getCells());
            cells.add(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);
        }

        return cells;
    }

    @Override
    public List<ItemLike> getAllPortables() {
        var portables = Platform.super.getAllPortables();

        if (isAddonLoaded(Addons.APPMEK)) {
            portables.addAll(AppMekItems.getPortables());
        }

        return portables;
    }

    @Override
    public void register() {
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.BLOCK)) {
                MEGABlocks.getBlocks().forEach(b -> {
                    ForgeRegistries.BLOCKS.register(b.id(), b.block());
                    ForgeRegistries.ITEMS.register(b.id(), b.asItem());
                });
            }

            if (event.getRegistryKey().equals(Registries.ITEM)) {
                MEGAItems.getItems().forEach(i -> ForgeRegistries.ITEMS.register(i.id(), i.asItem()));
            }

            if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MEGACreativeTab.ID, MEGACreativeTab.TAB);
            }

            if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
                MEGABlockEntities.getBlockEntityTypes().forEach(ForgeRegistries.BLOCK_ENTITY_TYPES::register);
            }

            if (event.getRegistryKey().equals(Registries.MENU)) {
                MEGAMenus.getMenuTypes().forEach(ForgeRegistries.MENU_TYPES::register);
            }
        });
    }

    @Override
    public void initUpgrades() {
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(Platform.super::initUpgrades);

            if (isAddonLoaded(Addons.APPMEK)) {
                event.enqueueWork(AppMekIntegration::initUpgrades);
            }
        });
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
    public void initLavaTransform() {
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> LavaTransformLogic.clearCache());
        MinecraftForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
            if (event.getPlayer() == null) LavaTransformLogic.clearCache();
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
    public void addIntegrationRecipe(
            Consumer<FinishedRecipe> writer, FinishedRecipe recipe, Addons addon, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(addon.getModId()))
                .addRecipe(recipe)
                .build(writer, id);
    }

    @Override
    public void addIntegrationRecipe(
            Consumer<FinishedRecipe> writer, RecipeBuilder builder, Addons addon, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(addon.getModId()))
                .addRecipe(builder::save)
                .build(writer, id);
    }

    public static class Client implements Platform.Client {
        @Override
        public void initScreens() {
            modEventBus.addListener((FMLClientSetupEvent event) -> screens());

            ModLoadingContext.get()
                    .registerExtensionPoint(
                            ConfigScreenHandler.ConfigScreenFactory.class,
                            () -> new ConfigScreenHandler.ConfigScreenFactory(
                                    (client, parent) -> AutoConfig.getConfigScreen(MEGAConfig.class, parent)
                                            .get()));
        }

        @Override
        public void initEnergyCellProps() {
            modEventBus.addListener((ModelEvent.RegisterGeometryLoaders event) -> energyCellProps());
        }

        @SuppressWarnings("deprecation")
        @Override
        public void initCraftingUnitModels() {
            for (var type : MEGACraftingUnitType.values()) {
                BuiltInModelHooks.addBuiltInModel(
                        MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                        new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));

                modEventBus.addListener((FMLClientSetupEvent event) ->
                        ItemBlockRenderTypes.setRenderLayer(type.getDefinition().block(), RenderType.cutout()));
            }
        }

        @Override
        public void initItemColours(ItemColor color, List<ItemLike> items) {
            modEventBus.addListener(
                    (RegisterColorHandlersEvent.Item event) -> event.register(color, items.toArray(new ItemLike[0])));
        }

        @Override
        public BakedModel createCellModel(Item cell, BlockOrientation orientation) {
            var driveModel = BakedModelUnwrapper.unwrap(
                    Minecraft.getInstance()
                            .getModelManager()
                            .getBlockModelShaper()
                            .getBlockModel(AEBlocks.DRIVE.block().defaultBlockState()),
                    DriveBakedModel.class);

            if (driveModel == null) {
                return null;
            }

            return new DelegateBakedModel(driveModel.getCellChassisModel(cell)) {
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

                    var quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));

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
            };
        }
    }
}

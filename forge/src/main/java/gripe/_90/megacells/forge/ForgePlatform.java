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
import gripe._90.megacells.integration.arseng.ArsEngIntegration;
import gripe._90.megacells.integration.arseng.ArsEngItems;
import gripe._90.megacells.misc.CompressionService;
import gripe._90.megacells.misc.LavaTransformLogic;

public final class ForgePlatform implements Platform {
    private static final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    @Override
    public List<ItemLike> getAllCells() {
        var cells = Platform.super.getAllCells();

        if (isAddonLoaded(Addons.APPMEK)) {
            cells.addAll(AppMekItems.getCells());
            cells.add(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);
        }

        if (isAddonLoaded(Addons.ARSENG)) {
            cells.addAll(ArsEngItems.getCells());
        }

        return cells;
    }

    @Override
    public List<ItemLike> getAllPortables() {
        var portables = Platform.super.getAllPortables();

        if (isAddonLoaded(Addons.APPMEK)) {
            portables.addAll(AppMekItems.getPortables());
        }

        if (isAddonLoaded(Addons.ARSENG)) {
            portables.addAll(ArsEngItems.getPortables());
        }

        return portables;
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
}

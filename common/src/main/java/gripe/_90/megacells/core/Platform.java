package gripe._90.megacells.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import appeng.api.orientation.BlockOrientation;
import appeng.api.upgrades.Upgrades;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.client.gui.implementations.InterfaceScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.init.client.InitScreens;

import gripe._90.megacells.client.gui.CellDockScreen;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appbot.AppBotIntegration;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

public interface Platform {
    void addIntegrationRecipe(
            Consumer<FinishedRecipe> writer, FinishedRecipe recipe, Addons addon, ResourceLocation id);

    void addIntegrationRecipe(
            Consumer<FinishedRecipe> writer, RecipeBuilder builder, Addons addon, ResourceLocation id);
}

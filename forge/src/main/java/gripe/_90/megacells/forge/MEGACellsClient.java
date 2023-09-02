package gripe._90.megacells.forge;

import java.util.ArrayList;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.block.networking.EnergyCellBlockItem;
import appeng.client.gui.implementations.InterfaceScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.crafting.CraftingMonitorRenderer;
import appeng.core.AppEng;
import appeng.hooks.BuiltInModelHooks;
import appeng.init.client.InitScreens;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

import me.shedaniel.autoconfig.AutoConfig;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAConfig;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class MEGACellsClient {
    static void init() {
        initBuiltInModels();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(MEGACellsClient::initScreens);
        bus.addListener(MEGACellsClient::initRenderTypes);
        bus.addListener(MEGACellsClient::initModels);
        bus.addListener(MEGACellsClient::initItemColors);

        // the absolute state
        ModLoadingContext.get()
                .registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(
                                (client, parent) -> AutoConfig.getConfigScreen(MEGAConfig.class, parent)
                                        .get()));
    }

    private static void initScreens(FMLClientSetupEvent ignoredEvent) {
        InitScreens.register(
                MEGAInterfaceMenu.TYPE,
                InterfaceScreen<MEGAInterfaceMenu>::new,
                "/screens/megacells/mega_interface.json");
        InitScreens.register(
                MEGAPatternProviderMenu.TYPE,
                PatternProviderScreen<MEGAPatternProviderMenu>::new,
                "/screens/megacells/mega_pattern_provider.json");
    }

    private static void initRenderTypes(FMLClientSetupEvent ignoredEvent) {
        for (var type : MEGACraftingUnitType.values()) {
            ItemBlockRenderTypes.setRenderLayer(type.getDefinition().block(), RenderType.cutout());
        }
    }

    private static void initBuiltInModels() {
        for (var type : MEGACraftingUnitType.values()) {
            BuiltInModelHooks.addBuiltInModel(
                    MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                    new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));
        }
    }

    private static void initModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);

        ItemProperties.register(
                MEGABlocks.MEGA_ENERGY_CELL.asItem(), AppEng.makeId("fill_level"), (is, level, entity, seed) -> {
                    var energyCell = (EnergyCellBlockItem) MEGABlocks.MEGA_ENERGY_CELL.asItem();

                    double curPower = energyCell.getAECurrentPower(is);
                    double maxPower = energyCell.getAEMaxPower(is);

                    return (float) (curPower / maxPower);
                });
    }

    private static void initItemColors(RegisterColorHandlersEvent.Item event) {
        var cells = new ArrayList<>(MEGAItems.getItemCells());
        cells.addAll(MEGAItems.getFluidCells());
        cells.add(MEGAItems.BULK_ITEM_CELL);

        var portables = new ArrayList<>(MEGAItems.getItemPortables());
        portables.addAll(MEGAItems.getFluidPortables());

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            cells.addAll(AppMekItems.getCells());
            portables.addAll(AppMekItems.getPortables());
            cells.add(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);
        }

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            cells.addAll(AppBotItems.getCells());
            portables.addAll(AppBotItems.getPortables());
        }

        event.register(BasicStorageCell::getColor, cells.toArray(new ItemLike[0]));
        event.register(PortableCellItem::getColor, portables.toArray(new ItemLike[0]));
    }
}

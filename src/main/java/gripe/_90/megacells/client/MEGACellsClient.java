package gripe._90.megacells.client;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import appeng.api.client.StorageCellModels;
import appeng.client.InitScreens;
import appeng.client.api.renderer.parts.RegisterPartRendererEvent;
import appeng.client.gui.implementations.InterfaceScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.renderer.blockentity.CraftingMonitorRenderer;
import appeng.menu.implementations.InterfaceMenu;
import appeng.menu.implementations.PatternProviderMenu;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.client.render.CellDockPartRenderer;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.client.render.PortableCellWorkbenchClientTooltipComponent;
import gripe._90.megacells.client.screen.CellDockScreen;
import gripe._90.megacells.client.screen.PortableCellWorkbenchScreen;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.item.cell.PortableCellWorkbenchTooltipComponent;
import gripe._90.megacells.item.part.CellDockPart;

@Mod(value = MEGACells.MODID, dist = Dist.CLIENT)
public class MEGACellsClient {
    public MEGACellsClient(IEventBus eventBus) {
        eventBus.addListener(MEGACellsClient::initScreens);
        eventBus.addListener(MEGACellsClient::initStorageCellModels);
        eventBus.addListener(MEGACellsClient::registerBlockStateModels);
        eventBus.addListener(MEGACellsClient::initBlockEntityRenderers);
        eventBus.addListener(MEGACellsClient::initTooltipComponents);
        eventBus.addListener(MEGACellsClient::initPartRenderers);
        eventBus.addListener(MEGACellsClient::initResourcePackFinder);
    }

    private static void initPartRenderers(RegisterPartRendererEvent event) {
        event.register(CellDockPart.class, new CellDockPartRenderer());
    }

    private static void registerBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(MEGACraftingUnitModelProvider.Unbaked.ID, MEGACraftingUnitModelProvider.Unbaked.MAP_CODEC);
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                MEGAMenus.MEGA_INTERFACE.get(),
                InterfaceScreen<InterfaceMenu>::new,
                "/screens/megacells/mega_interface.json");
        InitScreens.register(
                event,
                MEGAMenus.MEGA_PATTERN_PROVIDER.get(),
                PatternProviderScreen<PatternProviderMenu>::new,
                "/screens/megacells/mega_pattern_provider.json");
        InitScreens.register(
                event, MEGAMenus.CELL_DOCK.get(), CellDockScreen::new, "/screens/megacells/cell_dock.json");
        InitScreens.register(
                event,
                MEGAMenus.PORTABLE_CELL_WORKBENCH.get(),
                PortableCellWorkbenchScreen::new,
                "/screens/megacells/portable_cell_workbench.json");
    }

    private static void initBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MEGABlockEntities.MEGA_CRAFTING_MONITOR.get(), CraftingMonitorRenderer::new);
    }

    private static void initTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(PortableCellWorkbenchTooltipComponent.class, PortableCellWorkbenchClientTooltipComponent::new);
    }

    private static void initResourcePackFinder(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addPackFinders(
                    MEGACells.makeId("optional_cell_colours"),
                    PackType.CLIENT_RESOURCES,
                    MEGATranslations.ClassicCellColours.text(),
                    PackSource.BUILT_IN,
                    false,
                    Pack.Position.TOP);
        }
    }

    private static void initStorageCellModels(FMLCommonSetupEvent event) {
        // Has to be done in common setup, otherwise textures are broken when first entering a world until one forces a
        // resource pack reload.
        event.enqueueWork(() -> {
            var modelPrefix = "block/drive/cells/";

            for (var cell : MEGAItems.getTieredCells()) {
                StorageCellModels.registerModel(
                        cell.item(),
                        MEGACells.makeId(modelPrefix + cell.tier().namePrefix() + "_" + cell.keyType() + "_cell"));
            }

            StorageCellModels.registerModel(
                    MEGAItems.BULK_ITEM_CELL,
                    MEGACells.makeId(modelPrefix + MEGAItems.BULK_ITEM_CELL.id().getPath()));

            StorageCellModels.registerModel(
                    MEGAItems.RADIOACTIVE_CHEMICAL_CELL,
                    MEGACells.makeId(modelPrefix
                            + MEGAItems.RADIOACTIVE_CHEMICAL_CELL.id().getPath()));
        });
    }
}

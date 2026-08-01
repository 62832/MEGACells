package gripe._90.megacells.client;

import java.util.ArrayList;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import appeng.api.client.StorageCellModels;
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
import appeng.menu.implementations.InterfaceMenu;
import appeng.menu.implementations.PatternProviderMenu;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.client.render.PortableCellWorkbenchClientTooltipComponent;
import gripe._90.megacells.client.screen.CellDockScreen;
import gripe._90.megacells.client.screen.PortableCellWorkbenchScreen;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.appliede.AppliedEIntegration;
import gripe._90.megacells.item.cell.PortableCellWorkbenchTooltipComponent;

@Mod(value = MEGACells.MODID, dist = Dist.CLIENT)
public class MEGACellsClient {
    public MEGACellsClient(IEventBus eventBus) {
        initCraftingUnitModels();

        eventBus.addListener(MEGACellsClient::initScreens);
        eventBus.addListener(MEGACellsClient::initBlockEntityRenderers);
        eventBus.addListener(MEGACellsClient::initEnergyCellProps);
        eventBus.addListener(MEGACellsClient::initStorageCellModels);
        eventBus.addListener(MEGACellsClient::initItemColours);
        eventBus.addListener(MEGACellsClient::initTooltipComponents);
        eventBus.addListener(MEGACellsClient::initResourcePackFinder);
    }

    private static void initCraftingUnitModels() {
        for (var type : MEGACraftingUnitType.values()) {
            BuiltInModelHooks.addBuiltInModel(
                    MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                    new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));
        }
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

        if (Addons.APPLIEDE.isLoaded()) {
            InitScreens.register(
                    event,
                    AppliedEIntegration.MEGA_EMC_INTERFACE_MENU.get(),
                    gripe._90.appliede.client.screen.EMCInterfaceScreen<gripe._90.appliede.menu.EMCInterfaceMenu>::new,
                    "/screens/megacells/mega_emc_interface.json");
        }
    }

    private static void initBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MEGABlockEntities.MEGA_CRAFTING_MONITOR.get(), CraftingMonitorRenderer::new);
    }

    private static void initEnergyCellProps(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                MEGABlocks.MEGA_ENERGY_CELL.asItem(), AppEng.makeId("fill_level"), (is, level, entity, i) -> {
                    var energyCell = (EnergyCellBlockItem) MEGABlocks.MEGA_ENERGY_CELL.asItem();

                    double curPower = energyCell.getAECurrentPower(is);
                    double maxPower = energyCell.getAEMaxPower(is);

                    return (float) (curPower / maxPower);
                }));
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
                    MEGAItems.BULK_FLUID_CELL,
                    MEGACells.makeId(modelPrefix + MEGAItems.BULK_FLUID_CELL.id().getPath()));

            StorageCellModels.registerModel(
                    MEGAItems.RADIOACTIVE_CHEMICAL_CELL,
                    MEGACells.makeId(modelPrefix
                            + MEGAItems.RADIOACTIVE_CHEMICAL_CELL.id().getPath()));
        });
    }

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        var standardCells = new ArrayList<ItemLike>();
        var portableCells = new ArrayList<ItemLike>();

        for (var cell : MEGAItems.getTieredCells()) {
            (cell.portable() ? portableCells : standardCells).add(cell.item());
        }

        standardCells.add(MEGAItems.BULK_ITEM_CELL);
        standardCells.add(MEGAItems.BULK_FLUID_CELL);
        standardCells.add(MEGAItems.RADIOACTIVE_CHEMICAL_CELL);

        event.register(
                (stack, tintIndex) -> FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex)),
                standardCells.toArray(new ItemLike[0]));
        event.register(
                (stack, tintIndex) -> FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex)),
                portableCells.toArray(new ItemLike[0]));
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
}

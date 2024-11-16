package gripe._90.megacells.client;

import java.util.ArrayList;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

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

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.client.screen.CellDockScreen;
import gripe._90.megacells.client.screen.PortableCellWorkbenchScreen;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

@Mod(value = MEGACells.MODID, dist = Dist.CLIENT)
public class MEGACellsClient {
    public MEGACellsClient(IEventBus eventBus) {
        eventBus.addListener(MEGACellsClient::initScreens);
        eventBus.addListener(MEGACellsClient::initCraftingUnitModels);
        eventBus.addListener(MEGACellsClient::initEnergyCellProps);
        eventBus.addListener(MEGACellsClient::initStorageCellModels);
        eventBus.addListener(MEGACellsClient::initItemColours);
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                MEGAMenus.MEGA_INTERFACE,
                InterfaceScreen<MEGAInterfaceMenu>::new,
                "/screens/megacells/mega_interface.json");
        InitScreens.register(
                event,
                MEGAMenus.MEGA_PATTERN_PROVIDER,
                PatternProviderScreen<MEGAPatternProviderMenu>::new,
                "/screens/megacells/mega_pattern_provider.json");
        InitScreens.register(event, MEGAMenus.CELL_DOCK, CellDockScreen::new, "/screens/megacells/cell_dock.json");
        InitScreens.register(
                event,
                MEGAMenus.PORTABLE_CELL_WORKBENCH,
                PortableCellWorkbenchScreen::new,
                "/screens/megacells/portable_cell_workbench.json");
    }

    private static void initCraftingUnitModels(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var type : MEGACraftingUnitType.values()) {
                BuiltInModelHooks.addBuiltInModel(
                        MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                        new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));
            }

            BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR.get(), CraftingMonitorRenderer::new);
        });
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

    private static void initStorageCellModels(FMLClientSetupEvent event) {
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

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        var standardCells = new ArrayList<ItemLike>();
        var portableCells = new ArrayList<ItemLike>();

        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.portable()) {
                portableCells.add(cell.item());
            } else {
                standardCells.add(cell.item());
            }
        }

        standardCells.add(MEGAItems.BULK_ITEM_CELL);
        standardCells.add(MEGAItems.RADIOACTIVE_CHEMICAL_CELL);

        event.register(
                (stack, tintIndex) -> FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex)),
                standardCells.toArray(new ItemLike[0]));
        event.register(
                (stack, tintIndex) -> FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex)),
                portableCells.toArray(new ItemLike[0]));
    }
}

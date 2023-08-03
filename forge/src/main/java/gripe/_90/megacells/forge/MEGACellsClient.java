package gripe._90.megacells.forge;

import java.util.ArrayList;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.block.networking.EnergyCellBlockItem;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.crafting.CraftingMonitorRenderer;
import appeng.core.AppEng;
import appeng.hooks.BuiltInModelHooks;
import appeng.init.client.InitScreens;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.block.MEGAPatternProviderBlock;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appbot.AppBotItems;
import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.util.Addons;
import gripe._90.megacells.util.Utils;

public class MEGACellsClient {
    public MEGACellsClient() {
        initBuiltInModels();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::initScreens);
        bus.addListener(this::initRenderTypes);
        bus.addListener(this::initModels);
        bus.addListener(this::initItemColors);
    }

    private void initScreens(FMLClientSetupEvent ignoredEvent) {
        InitScreens.register(
                MEGAPatternProviderBlock.MENU,
                PatternProviderScreen<MEGAPatternProviderBlock.Menu>::new,
                "/screens/megacells/mega_pattern_provider.json");
    }

    private void initRenderTypes(FMLClientSetupEvent ignoredEvent) {
        for (var type : MEGACraftingUnitType.values()) {
            ItemBlockRenderTypes.setRenderLayer(type.getDefinition().block(), RenderType.cutout());
        }
    }

    private void initBuiltInModels() {
        for (var type : MEGACraftingUnitType.values()) {
            BuiltInModelHooks.addBuiltInModel(
                    AppEng.makeId("block/crafting/mega_" + type.getAffix() + "_formed"),
                    new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));
        }
    }

    private void initModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);

        ItemProperties.register(
                MEGABlocks.MEGA_ENERGY_CELL.asItem(), AppEng.makeId("fill_level"), (is, level, entity, seed) -> {
                    var energyCell = (EnergyCellBlockItem) MEGABlocks.MEGA_ENERGY_CELL.asItem();

                    double curPower = energyCell.getAECurrentPower(is);
                    double maxPower = energyCell.getAEMaxPower(is);

                    return (float) (curPower / maxPower);
                });
    }

    private void initItemColors(RegisterColorHandlersEvent.Item event) {
        var cells = new ArrayList<>(MEGAItems.getItemCells());
        cells.addAll(MEGAItems.getFluidCells());
        cells.add(MEGAItems.BULK_ITEM_CELL);

        var portables = new ArrayList<>(MEGAItems.getItemPortables());
        portables.addAll(MEGAItems.getFluidPortables());

        if (Utils.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            cells.addAll(AppMekItems.getCells());
            portables.addAll(AppMekItems.getPortables());
            cells.add(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);
        }

        if (Utils.PLATFORM.isAddonLoaded(Addons.APPBOT)) {
            cells.addAll(AppBotItems.getCells());
            portables.addAll(AppBotItems.getPortables());
        }

        event.register(BasicStorageCell::getColor, cells.toArray(new ItemLike[0]));
        event.register(PortableCellItem::getColor, portables.toArray(new ItemLike[0]));
    }
}

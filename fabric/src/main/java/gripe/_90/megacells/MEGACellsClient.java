package gripe._90.megacells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.ItemLike;

import appeng.api.IAEAddonEntrypoint;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.render.SimpleModelLoader;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.crafting.CraftingMonitorRenderer;
import appeng.client.render.crafting.MonitorBakedModel;
import appeng.client.render.model.AutoRotatingBakedModel;
import appeng.core.AppEng;
import appeng.hooks.ModelsReloadCallback;
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
import gripe._90.megacells.util.Utils;

@Environment(EnvType.CLIENT)
public class MEGACellsClient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        initScreens();
        initBlockModels();
        initItemModels();
        initItemColors();
    }

    private void initScreens() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            InitScreens.register(
                    MEGAPatternProviderBlock.MENU,
                    PatternProviderScreen<MEGAPatternProviderBlock.Menu>::new,
                    "/screens/megacells/mega_pattern_provider.json");
        });
    }

    private void initBlockModels() {
        for (var type : MEGACraftingUnitType.values()) {
            ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new SimpleModelLoader<>(
                    Utils.makeId("block/crafting/" + type.getAffix() + "_formed"),
                    () -> new CraftingCubeModel(new MEGACraftingUnitModelProvider(type))));

            BlockRenderLayerMap.INSTANCE.putBlock(type.getDefinition().block(), RenderType.cutout());
        }

        BlockEntityRenderers.register(MEGABlockEntities.MEGA_CRAFTING_MONITOR, CraftingMonitorRenderer::new);

        ModelsReloadCallback.EVENT.register(modelRegistry -> {
            var customizers = new HashMap<String, Function<BakedModel, BakedModel>>();
            customizers.put(
                    MEGABlocks.CRAFTING_MONITOR.id().getPath(),
                    model -> model instanceof MonitorBakedModel ? model : new AutoRotatingBakedModel(model));
            customizers.put(MEGABlocks.MEGA_PATTERN_PROVIDER.id().getPath(), AutoRotatingBakedModel::new);

            for (var location : modelRegistry.keySet()) {
                if (!location.getNamespace().equals(Utils.MODID)) {
                    continue;
                }

                var originalModel = modelRegistry.get(location);
                var customizer = customizers.get(location.getPath());

                if (customizer != null) {
                    var newModel = customizer.apply(originalModel);
                    modelRegistry.put(location, newModel);
                }
            }
        });
    }

    private void initItemModels() {
        ItemProperties.register(
                MEGABlocks.MEGA_ENERGY_CELL.asItem(), AppEng.makeId("fill_level"), (is, level, entity, seed) -> {
                    var energyCell = (EnergyCellBlockItem) MEGABlocks.MEGA_ENERGY_CELL.asItem();

                    double curPower = energyCell.getAECurrentPower(is);
                    double maxPower = energyCell.getAEMaxPower(is);

                    return (float) (curPower / maxPower);
                });
    }

    private void initItemColors() {
        var cells = new ArrayList<>(MEGAItems.getItemCells());
        cells.addAll(MEGAItems.getFluidCells());
        cells.add(MEGAItems.BULK_ITEM_CELL);

        var portables = new ArrayList<>(MEGAItems.getItemPortables());
        portables.addAll(MEGAItems.getFluidPortables());

        if (Utils.PLATFORM.isModLoaded("appbot")) {
            cells.addAll(AppBotItems.getCells());
            portables.addAll(AppBotItems.getPortables());
        }

        ColorProviderRegistry.ITEM.register(BasicStorageCell::getColor, cells.toArray(new ItemLike[0]));
        ColorProviderRegistry.ITEM.register(PortableCellItem::getColor, portables.toArray(new ItemLike[0]));
    }
}

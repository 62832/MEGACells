package ninety.megacells;

import java.util.stream.Stream;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.datagen.MEGADataGenerators;
import ninety.megacells.init.MEGACellsClient;
import ninety.megacells.integration.appmek.MEGAMekIntegration;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.util.MEGACellType;
import ninety.megacells.util.MEGACellsUtil;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

@Mod(MEGACells.MODID)
public class MEGACells {

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MEGACells.MODID, path);
    }

    public static String getItemPath(Item item) {
        return item.getRegistryName().getPath();
    }

    public static final String MODID = "megacells";

    public MEGACells() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MEGAItems.init(bus);

        bus.addGenericListener(Item.class, MEGAMekIntegration::registerItems);

        bus.addListener(MEGADataGenerators::onGatherData);
        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(this::initCellModels);
            event.enqueueWork(this::initUpgrades);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MEGACellsClient::initialize);
    }

    private void initCellModels() {
        for (var cell : Stream.concat(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream()).toList()) {
            StorageCellModels.registerModel(cell,
                    MEGACellsUtil.makeId("block/drive/cells/" + MEGACellsUtil.getItemPath(cell)));
        }
        for (var portableItemCell : MEGACellType.ITEM.getPortableCells()) {
            StorageCellModels.registerModel(portableItemCell,
                    MEGACellsUtil.makeId("block/drive/cells/portable_mega_item_cell"));
        }
        for (var portableFluidCell : MEGACellType.FLUID.getPortableCells()) {
            StorageCellModels.registerModel(portableFluidCell,
                    MEGACellsUtil.makeId("block/drive/cells/portable_mega_fluid_cell"));
        }
        MEGAMekIntegration.initCellModels();
    }

    private void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        for (var itemCell : MEGACellType.ITEM.getCells()) {
            Upgrades.add(AEItems.FUZZY_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, itemCell, 1, storageCellGroup);
        }

        for (var fluidCell : MEGACellType.FLUID.getCells()) {
            Upgrades.add(AEItems.INVERTER_CARD, fluidCell, 1, storageCellGroup);
        }

        for (var portableCell : Stream.concat(MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream()).toList()) {
            Upgrades.add(AEItems.FUZZY_CARD, portableCell, 1, portableCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, portableCell, 1, portableCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, portableCell, 2, portableCellGroup);
        }
        MEGAMekIntegration.initUpgrades();
    }

}

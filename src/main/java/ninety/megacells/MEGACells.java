package ninety.megacells;

import java.util.stream.Stream;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import ninety.megacells.datagen.MEGADataGenerators;
import ninety.megacells.init.MEGACellsClient;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.util.MEGACellType;
import ninety.megacells.util.MEGACellsUtil;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

@Mod(MEGACells.MODID)
public class MEGACells {

    public static final String MODID = "megacells";

    public MEGACells() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MEGAItems.init(bus);

        bus.addListener(MEGADataGenerators::onGatherData);
        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(this::initCellModels);
            event.enqueueWork(this::initUpgrades);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MEGACellsClient::initialize);
    }

    private void initCellModels() {
        for (var cell : Stream.of(
                MEGACellType.ITEM.getCells().stream(),
                MEGACellType.FLUID.getCells().stream(),
                ChemicalCellType.TYPE.getCells().stream()).flatMap(s -> s).toList()) {
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
        for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
            StorageCellModels.registerModel(portable,
                    MEGACellsUtil.makeId("block/drive/cells/portable_mega_item_cell"));
        }
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

        for (var fluidCell : Stream.concat(
                MEGACellType.FLUID.getCells().stream(), ChemicalCellType.TYPE.getCells().stream()).toList()) {
            Upgrades.add(AEItems.INVERTER_CARD, fluidCell, 1, storageCellGroup);
        }

        for (var portableCell : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream(),
                ChemicalCellType.TYPE.getPortableCells().stream()).flatMap(s -> s).toList()) {
            Upgrades.add(AEItems.FUZZY_CARD, portableCell, 1, portableCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, portableCell, 1, portableCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, portableCell, 2, portableCellGroup);
        }
    }

}

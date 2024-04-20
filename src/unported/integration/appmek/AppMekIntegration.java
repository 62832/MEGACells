package gripe._90.megacells.integration.appmek;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import net.minecraftforge.fml.loading.FMLEnvironment;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import me.ramidzkh.mekae2.AMItems;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.appmek.item.RadioactiveCellItem;

public final class AppMekIntegration {
    public static void init() {
        StorageCells.addCellHandler(RadioactiveCellItem.HANDLER);

        if (FMLEnvironment.dist.isClient()) {
            Stream.of(AppMekItems.getCells(), AppMekItems.getPortables())
                    .flatMap(Collection::stream)
                    .forEach(c -> StorageCellModels.registerModel(
                            c, MEGACells.makeId("block/drive/cells/mega_chemical_cell")));

            StorageCellModels.registerModel(
                    AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem(),
                    MEGACells.makeId("block/drive/cells/radioactive_chemical_cell"));
        }
    }

    public static void initUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        AppMekItems.getCells().forEach(cell -> {
            Upgrades.add(AEItems.INVERTER_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, cell, 1, storageCellGroup);
        });

        AppMekItems.getPortables().forEach(portable -> {
            Upgrades.add(AEItems.INVERTER_CARD, portable, 1, portableCellGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable, 2, portableCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, portable, 1, portableCellGroup);
            Upgrades.add(AEItems.VOID_CARD, portable, 1, portableCellGroup);
        });

        for (var portable : List.of(
                AMItems.PORTABLE_CHEMICAL_CELL_1K,
                AMItems.PORTABLE_CHEMICAL_CELL_4K,
                AMItems.PORTABLE_CHEMICAL_CELL_16K,
                AMItems.PORTABLE_CHEMICAL_CELL_64K,
                AMItems.PORTABLE_CHEMICAL_CELL_256K)) {
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portable.get(), 2, portableCellGroup);
        }
    }
}

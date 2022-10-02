package ninety.megacells.init;

import java.util.stream.Stream;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import ninety.megacells.integration.appmek.AppMekCellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.core.MEGACellType;

public class InitUpgrades {
    public static void init() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableCellGroup = GuiText.PortableCells.getTranslationKey();

        for (var itemCell : MEGACellType.ITEM.getCells()) {
            Upgrades.add(AEItems.FUZZY_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemCell, 1, storageCellGroup);
            Upgrades.add(AEItems.VOID_CARD, itemCell, 1, storageCellGroup);
        }

        for (var fluidCell : Stream.concat(
                MEGACellType.FLUID.getCells().stream(), AppMekCellType.CHEMICAL.getCells().stream()).toList()) {
            Upgrades.add(AEItems.INVERTER_CARD, fluidCell, 1, storageCellGroup);
        }

        for (var portableCell : Stream.of(
                MEGACellType.ITEM.getPortableCells().stream(),
                MEGACellType.FLUID.getPortableCells().stream(),
                AppMekCellType.CHEMICAL.getPortableCells().stream()).flatMap(s -> s).toList()) {
            Upgrades.add(AEItems.FUZZY_CARD, portableCell, 1, portableCellGroup);
            Upgrades.add(AEItems.INVERTER_CARD, portableCell, 1, portableCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, portableCell, 2, portableCellGroup);
        }
    }
}

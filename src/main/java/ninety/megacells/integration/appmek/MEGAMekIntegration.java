package ninety.megacells.integration.appmek;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModList;

import ninety.megacells.item.MEGAItems;
import ninety.megacells.item.MEGAPortableCell;
import ninety.megacells.item.MEGAStorageCell;
import ninety.megacells.item.util.MEGACellTier;
import ninety.megacells.util.MEGACellsUtil;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.materials.MaterialItem;

public class MEGAMekIntegration {
    protected static Item cell(MEGACellTier tier) {
        return new MEGAStorageCell(MEGAItems.props.stacksTo(1), tier, ChemicalCellType.TYPE)
                .setRegistryName(MEGACellsUtil.makeId("chemical_storage_cell_" + tier.affix));
    }

    protected static Item portable(MEGACellTier tier) {
        return new MEGAPortableCell(MEGAItems.props.stacksTo(1), tier, ChemicalCellType.TYPE)
                .setRegistryName(MEGACellsUtil.makeId("portable_chemical_cell_" + tier.affix));
    }

    private static final Item HOUSING = new MaterialItem(MEGAItems.props)
            .setRegistryName(MEGACellsUtil.makeId("mega_chemical_cell_housing"));

    public static void registerItems(RegistryEvent.Register<Item> event) {
        if (isLoaded()) {
            event.getRegistry().registerAll(
                    HOUSING,
                    cell(MEGACellTier._1M),
                    cell(MEGACellTier._4M),
                    cell(MEGACellTier._16M),
                    cell(MEGACellTier._64M),
                    cell(MEGACellTier._256M),
                    portable(MEGACellTier._1M),
                    portable(MEGACellTier._4M),
                    portable(MEGACellTier._16M),
                    portable(MEGACellTier._64M),
                    portable(MEGACellTier._256M));
        }
    }

    public static void initUpgrades() {
        if (isLoaded()) {
            var storageCellGroup = GuiText.StorageCells.getTranslationKey();
            var portableCellGroup = GuiText.PortableCells.getTranslationKey();

            for (var cell : ChemicalCellType.TYPE.getCells()) {
                Upgrades.add(AEItems.INVERTER_CARD, cell, 1, storageCellGroup);
            }
            for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
                Upgrades.add(AEItems.FUZZY_CARD, portable, 1, portableCellGroup);
                Upgrades.add(AEItems.INVERTER_CARD, portable, 1, portableCellGroup);
                Upgrades.add(AEItems.ENERGY_CARD, portable, 2, portableCellGroup);
            }
        }
    }

    public static void initCellModels() {
        if (isLoaded()) {
            for (var cell : ChemicalCellType.TYPE.getCells()) {
                StorageCellModels.registerModel(cell,
                        MEGACellsUtil.makeId("block/drive/cells/" + MEGACellsUtil.getItemPath(cell)));
            }
            for (var portable : ChemicalCellType.TYPE.getPortableCells()) {
                StorageCellModels.registerModel(portable,
                        MEGACellsUtil.makeId("block/drive/cells/portable_mega_item_cell"));
            }
        }
    }

    public static void initItemColors(ColorHandlerEvent.Item event) {
        if (isLoaded()) {
            for (var cell : ChemicalCellType.TYPE.getCells()) {
                event.getItemColors().register(MEGAStorageCell::getColor, cell);
            }
            for (var cell : ChemicalCellType.TYPE.getPortableCells()) {
                event.getItemColors().register(MEGAPortableCell::getColor, cell);
            }
        }
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded("appmek");
    }
}

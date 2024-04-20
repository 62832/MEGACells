package gripe._90.megacells.integration.arseng;

import java.util.Collection;
import java.util.stream.Stream;

import net.minecraftforge.fml.loading.FMLEnvironment;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import gripe._90.megacells.MEGACells;

public class ArsEngIntegration {
    public static void init() {
        if (FMLEnvironment.dist.isClient()) {
            Stream.of(ArsEngItems.getCells(), ArsEngItems.getPortables())
                    .flatMap(Collection::stream)
                    .forEach(c ->
                            StorageCellModels.registerModel(c, MEGACells.makeId("block/drive/cells/mega_source_cell")));
        }
    }

    public static void initUpgrades() {
        ArsEngItems.getCells()
                .forEach(cell -> Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey()));
        ArsEngItems.getPortables().forEach(cell -> {
            Upgrades.add(AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
        });
    }
}

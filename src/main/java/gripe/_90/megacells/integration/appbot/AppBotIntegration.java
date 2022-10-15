package gripe._90.megacells.integration.appbot;

import net.fabricmc.loader.impl.FabricLoaderImpl;

import appeng.api.client.StorageCellModels;

import gripe._90.megacells.MEGACells;

public final class AppBotIntegration {
    public static boolean isAppBotLoaded() {
        return FabricLoaderImpl.INSTANCE.isModLoaded("appbot");
    }

    public static void initCellModels() {
        if (isAppBotLoaded()) {
            for (var cell : AppBotCellType.MANA.getCells()) {
                StorageCellModels.registerModel(cell,
                        MEGACells.makeId("block/drive/cells/" + MEGACells.getItemPath(cell)));
            }
            for (var portable : AppBotCellType.MANA.getPortableCells()) {
                StorageCellModels.registerModel(portable,
                        MEGACells.makeId("block/drive/cells/portable_mega_mana_cell"));
            }
        }
    }
}

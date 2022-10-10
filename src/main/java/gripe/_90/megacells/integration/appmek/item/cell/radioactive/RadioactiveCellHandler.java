package gripe._90.megacells.integration.appmek.item.cell.radioactive;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.AppMekItems;

public class RadioactiveCellHandler implements ICellHandler {
    public static final RadioactiveCellHandler INSTANCE = new RadioactiveCellHandler();

    public static void init() {
        if (AppMekIntegration.isAppMekLoaded()) {
            StorageCells.addCellHandler(INSTANCE);
            StorageCellModels.registerModel(AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem(),
                    MEGACells.makeId("block/drive/cells/radioactive_chemical_cell"));
        }
    }

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof IRadioactiveCellItem;
    }

    @Nullable
    @Override
    public StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
        return RadioactiveCellInventory.createInventory(is, container);
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        // TODO
    }
}

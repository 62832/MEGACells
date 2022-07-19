package ninety.megacells.init;

import appeng.api.storage.StorageCells;
import ninety.megacells.item.core.bulk.BulkCellHandler;

public class InitStorageCells {
    public static void init() {
        StorageCells.addCellHandler(BulkCellHandler.INSTANCE);
    }
}

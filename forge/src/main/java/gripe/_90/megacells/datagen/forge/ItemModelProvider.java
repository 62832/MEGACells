package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.datagen.CommonModelSupplier;
import gripe._90.megacells.util.Utils;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");
    static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public ItemModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, Utils.MODID, efh);
        efh.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        efh.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        efh.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        CommonModelSupplier.FLAT_ITEMS.forEach(this::flatSingleLayer);
        CommonModelSupplier.STORAGE_CELLS.forEach(this::cell);
        CommonModelSupplier.PORTABLE_CELLS.forEach(this::portable);
        driveCell("mega_item_cell");
        driveCell("mega_fluid_cell");
        driveCell("mega_mana_cell");
        driveCell("bulk_item_cell");
    }

    private void driveCell(String texture) {
        withExistingParent("block/drive/cells/" + texture, DRIVE_CELL).texture("cell",
                Utils.makeId("block/drive/cells/" + texture));
    }

    private void cell(ItemDefinition<?> cell) {
        flatSingleLayer(cell, "cell/standard/").texture("layer1", STORAGE_CELL_LED);
    }

    private void portable(ItemDefinition<?> cell) {
        flatSingleLayer(cell, "cell/portable/").texture("layer1", PORTABLE_CELL_LED);
    }

    private void flatSingleLayer(ItemDefinition<?> item) {
        flatSingleLayer(item, "");
    }

    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item, String subfolder) {
        String path = item.id().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", Utils.makeId("item/" + subfolder + path));
    }
}

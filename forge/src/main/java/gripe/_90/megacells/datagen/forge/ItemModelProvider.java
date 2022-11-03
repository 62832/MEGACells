package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.datagen.CommonModelSupplier;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");
    static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public ItemModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, MEGACells.MODID, efh);
        efh.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        efh.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        efh.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        for (var item : CommonModelSupplier.FLAT_ITEMS) {
            flatSingleLayer(item);
        }

        for (var item : CommonModelSupplier.STORAGE_CELLS) {
            cell(item);
            var driveCellPath = "block/drive/cells/standard/" + item.id().getPath();
            withExistingParent(driveCellPath, DRIVE_CELL).texture("cell", MEGACells.makeId(driveCellPath));
        }

        for (var item : CommonModelSupplier.PORTABLE_CELLS) {
            portable(item);
        }
        withExistingParent("block/drive/cells/portable/portable_mega_item_cell", DRIVE_CELL).texture("cell",
                MEGACells.makeId("block/drive/cells/portable/portable_mega_item_cell"));
        withExistingParent("block/drive/cells/portable/portable_mega_fluid_cell", DRIVE_CELL).texture("cell",
                MEGACells.makeId("block/drive/cells/portable/portable_mega_fluid_cell"));
    }

    public void cell(ItemDefinition<?> cell) {
        flatSingleLayer(cell, "cell/standard/").texture("layer1", STORAGE_CELL_LED);
    }

    public void portable(ItemDefinition<?> cell) {
        flatSingleLayer(cell, "cell/portable/").texture("layer1", PORTABLE_CELL_LED);
    }

    public void flatSingleLayer(ItemDefinition<?> item) {
        flatSingleLayer(item, "");
    }

    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item, String subfolder) {
        String path = item.id().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", MEGACells.makeId("item/" + subfolder + path));
    }
}

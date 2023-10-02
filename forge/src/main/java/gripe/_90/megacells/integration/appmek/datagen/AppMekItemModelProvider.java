package gripe._90.megacells.integration.appmek.datagen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.integration.appmek.AppMekItems;
import gripe._90.megacells.util.Utils;

public class AppMekItemModelProvider extends ItemModelProvider {
    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");
    static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public AppMekItemModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, Utils.MODID, efh);
        efh.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        efh.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        efh.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING);

        for (var storage : AppMekItems.getCells()) {
            cell(storage);
        }

        for (var portable : AppMekItems.getPortables()) {
            portable(portable);
        }

        flatSingleLayer(AppMekItems.RADIOACTIVE_CELL_COMPONENT);
        cell(AppMekItems.RADIOACTIVE_CHEMICAL_CELL);

        driveCell("mega_chemical_cell");
        driveCell("radioactive_chemical_cell");
    }

    private void driveCell(String texture) {
        withExistingParent("block/drive/cells/" + texture, DRIVE_CELL)
                .texture("cell", Utils.makeId("block/drive/cells/" + texture));
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

    @NotNull
    @Override
    public String getName() {
        return super.getName() + "/appmek";
    }
}

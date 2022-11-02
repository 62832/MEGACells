package gripe._90.megacells.integration.appmek.datagen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.integration.appmek.item.AppMekItems;
import gripe._90.megacells.integration.appmek.item.cell.AppMekCellType;

public class AppMekItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    public AppMekItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MEGACells.MODID, existingFileHelper);
        existingFileHelper.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        existingFileHelper.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING.asItem());

        for (var storage : AppMekCellType.CHEMICAL.getCells()) {
            cell(storage);
        }
        flatSingleLayer(AppMekItems.RADIOACTIVE_CELL_COMPONENT.asItem());
        cell(AppMekItems.RADIOACTIVE_CHEMICAL_CELL.asItem());

        for (var portable : AppMekCellType.CHEMICAL.getPortableCells()) {
            portable(portable);
        }
    }

    private void cell(Item cell) {
        flatSingleLayer(cell, "/cells/standard").texture("layer1", STORAGE_CELL_LED);
    }

    private void portable(Item cell) {
        flatSingleLayer(cell, "/cells/portable").texture("layer1", PORTABLE_CELL_LED);
    }

    private void flatSingleLayer(Item item) {
        flatSingleLayer(item, "");
    }

    private ItemModelBuilder flatSingleLayer(Item item, String subfolder) {
        String path = MEGACells.getItemPath(item);
        return singleTexture(path, mcLoc("item/generated"), "layer0",
                MEGACells.makeId("item" + subfolder + "/" + path));
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "/appmek";
    }
}

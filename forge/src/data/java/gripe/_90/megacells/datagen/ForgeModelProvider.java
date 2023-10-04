package gripe._90.megacells.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.integration.appmek.AppMekItems;

class ForgeModelProvider extends ItemModelProvider {
    private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    private static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public ForgeModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, MEGACells.MODID, existing);
        existing.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        existing.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        existing.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            basicItem(AppMekItems.MEGA_CHEMICAL_CELL_HOUSING.asItem());

            AppMekItems.getCells().forEach(c -> cell(c, "standard", STORAGE_CELL_LED));
            AppMekItems.getPortables().forEach(c -> cell(c, "portable", PORTABLE_CELL_LED));

            basicItem(AppMekItems.RADIOACTIVE_CELL_COMPONENT.asItem());
            cell(AppMekItems.RADIOACTIVE_CHEMICAL_CELL, "standard", STORAGE_CELL_LED);

            driveCell("mega_chemical_cell");
            driveCell("radioactive_chemical_cell");
        }
    }

    private void cell(ItemDefinition<?> cell, String type, ResourceLocation led) {
        var path = cell.id().getPath();
        singleTexture(path, mcLoc("item/generated"), "layer0", MEGACells.makeId("item/cell/" + type + "/" + path))
                .texture("layer1", led);
    }

    private void driveCell(String texture) {
        var path = "block/drive/cells/" + texture;
        withExistingParent(path, DRIVE_CELL).texture("cell", path);
    }
}

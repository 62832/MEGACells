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
import gripe._90.megacells.integration.arseng.ArsEngItems;

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

            for (var cell : AppMekItems.getCells()) {
                cell(cell, "standard", STORAGE_CELL_LED);
                driveCell(cell, "mega_chemical_cell");
            }

            for (var portable : AppMekItems.getPortables()) {
                cell(portable, "portable", PORTABLE_CELL_LED);
                driveCell(portable, "mega_chemical_cell");
            }

            basicItem(AppMekItems.RADIOACTIVE_CELL_COMPONENT.asItem());
            cell(AppMekItems.RADIOACTIVE_CHEMICAL_CELL, "standard", STORAGE_CELL_LED);
            driveCell(AppMekItems.RADIOACTIVE_CHEMICAL_CELL, "radioactive_chemical_cell");
        }

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.ARSENG)) {
            basicItem(ArsEngItems.MEGA_SOURCE_CELL_HOUSING.asItem());

            for (var cell : ArsEngItems.getCells()) {
                cell(cell, "standard", STORAGE_CELL_LED);
                driveCell(cell, "mega_source_cell");
            }

            for (var portable : ArsEngItems.getPortables()) {
                cell(portable, "portable", PORTABLE_CELL_LED);
                driveCell(portable, "mega_source_cell");
            }
        }
    }

    private void cell(ItemDefinition<?> cell, String type, ResourceLocation led) {
        var path = cell.id().getPath();
        singleTexture(path, mcLoc("item/generated"), "layer0", MEGACells.makeId("item/cell/" + type + "/" + path))
                .texture("layer1", led);
    }

    private void driveCell(ItemDefinition<?> cell, String texture) {
        var prefix = "block/drive/cells/";
        withExistingParent(prefix + cell.id().getPath(), DRIVE_CELL).texture("cell", prefix + texture);
    }
}

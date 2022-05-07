package ninety.megacells.datagen;

import java.util.stream.Stream;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import ninety.megacells.MEGACells;
import ninety.megacells.item.MEGACellType;
import ninety.megacells.item.MEGAItems;
import ninety.megacells.util.MEGACellsUtil;

import appeng.core.AppEng;

public class MEGAItemModelProvider extends ItemModelProvider {

    private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    public MEGAItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MEGACells.MODID, existingFileHelper);
        existingFileHelper.trackGenerated(STORAGE_CELL_LED, TEXTURE);
    }

    protected void registerModels() {
        flatSingleLayer(MEGAItems.MEGA_ITEM_CELL_HOUSING.get());
        flatSingleLayer(MEGAItems.MEGA_FLUID_CELL_HOUSING.get());

        flatSingleLayer(MEGAItems.CELL_COMPONENT_1M.get());
        flatSingleLayer(MEGAItems.CELL_COMPONENT_4M.get());
        flatSingleLayer(MEGAItems.CELL_COMPONENT_16M.get());
        flatSingleLayer(MEGAItems.CELL_COMPONENT_64M.get());
        flatSingleLayer(MEGAItems.CELL_COMPONENT_256M.get());

        for (var storage : Stream.concat(MEGACellType.ITEM.getCells().stream(), MEGACellType.FLUID.getCells().stream())
                .toList()) {
            cell(storage);
        }
        for (var portable : Stream
                .concat(MEGACellType.ITEM.getPortableCells().stream(), MEGACellType.FLUID.getPortableCells().stream())
                .toList()) {
            portable(portable);
        }
    }

    private void cell(Item cell) {
        flatSingleLayer(cell).texture("layer1", STORAGE_CELL_LED);
    }

    private void portable(Item cell) {
        flatSingleLayer(cell).texture("layer1", PORTABLE_CELL_LED);
    }

    private ItemModelBuilder flatSingleLayer(Item item) {
        String path = MEGACellsUtil.getItemPath(item);
        return singleTexture(path, mcLoc("item/generated"), "layer0", MEGACellsUtil.makeId("item/" + path));
    }
}

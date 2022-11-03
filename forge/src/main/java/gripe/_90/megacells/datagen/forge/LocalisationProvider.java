package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.item.MEGAItems;

public class LocalisationProvider extends LanguageProvider {
    public LocalisationProvider(DataGenerator gen, String locale) {
        super(gen, MEGACells.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.megacells", "MEGA Cells");

        for (var item : MEGAItems.ITEMS) {
            add(item.asItem(), item.getEnglishName());
        }

        for (var block : MEGABlocks.BLOCKS) {
            add(block.block(), block.getEnglishName());
        }
    }
}

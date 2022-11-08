package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;

public class LocalisationProvider extends LanguageProvider {
    public LocalisationProvider(DataGenerator gen, String locale) {
        super(gen, MEGACells.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.megacells", "MEGA Cells");

        for (var item : MEGAItems.getItems()) {
            add(item.asItem(), item.getEnglishName());
        }

        for (var block : MEGABlocks.getBlocks()) {
            add(block.block(), block.getEnglishName());
        }
    }
}

package gripe._90.megacells.datagen.forge;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.util.Utils;

public class LocalisationProvider extends LanguageProvider {
    public LocalisationProvider(DataGenerator gen, String locale) {
        super(gen, Utils.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(MEGAItems.CREATIVE_TAB.getDisplayName().getString(), "MEGA Cells");
        MEGAItems.getItems().forEach(item -> add(item.asItem(), item.getEnglishName()));
        MEGABlocks.getBlocks().forEach(block -> add(block.block(), block.getEnglishName()));

        for (var t : MEGATranslations.values()) {
            add(t.getTranslationKey(), t.getEnglishText());
        }
    }
}

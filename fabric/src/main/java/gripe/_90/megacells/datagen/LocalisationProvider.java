package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;

public class LocalisationProvider extends FabricLanguageProvider {
    protected LocalisationProvider(FabricDataGenerator gen, String locale) {
        super(gen, locale);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(MEGAItems.CREATIVE_TAB, "MEGA Cells");
        MEGAItems.getItems().forEach(item -> builder.add(item.asItem(), item.getEnglishName()));
        MEGABlocks.getBlocks().forEach(block -> builder.add(block.block(), block.getEnglishName()));

        for (var t : MEGATranslations.values()) {
            builder.add(t.getTranslationKey(), t.getEnglishText());
        }
    }
}

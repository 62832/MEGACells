package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;

public class LocalisationProvider extends FabricLanguageProvider {
    protected LocalisationProvider(FabricDataGenerator gen, String locale) {
        super(gen, locale);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(MEGACells.CREATIVE_TAB, "MEGA Cells");

        for (var item : MEGAItems.getItems()) {
            builder.add(item.asItem(), item.getEnglishName());
        }

        for (var block : MEGABlocks.getBlocks()) {
            builder.add(block.block(), block.getEnglishName());
        }
    }
}

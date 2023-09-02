package gripe._90.megacells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;

class LocalisationProvider extends FabricLanguageProvider {
    protected LocalisationProvider(FabricDataOutput output) {
        super(output, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        MEGAItems.getItems().forEach(item -> builder.add(item.asItem(), item.getEnglishName()));
        MEGABlocks.getBlocks().forEach(block -> builder.add(block.block(), block.getEnglishName()));

        for (var translation : MEGATranslations.values()) {
            builder.add(translation.getTranslationKey(), translation.getEnglishText());
        }

        builder.add("text.autoconfig.%s.title".formatted(MEGACells.MODID), MEGATranslations.ModName.getEnglishText());
    }
}

package gripe._90.megacells.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;

class LocalisationProvider extends LanguageProvider {
    public LocalisationProvider(PackOutput output) {
        super(output, MEGACells.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        MEGAItems.getItems().forEach(item -> add(item.asItem(), item.getEnglishName()));
        MEGABlocks.getBlocks().forEach(block -> add(block.block(), block.getEnglishName()));

        for (var translation : MEGATranslations.values()) {
            add(translation.getTranslationKey(), translation.getEnglishText());
        }
    }
}

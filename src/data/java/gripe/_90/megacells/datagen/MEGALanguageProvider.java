package gripe._90.megacells.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;
import org.jetbrains.annotations.NotNull;

public class MEGALanguageProvider extends LanguageProvider {
    public MEGALanguageProvider(PackOutput output) {
        super(output, MEGACells.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var item : MEGAItems.getItems()) {
            add(item.asItem(), item.getEnglishName());
        }

        for (var block : MEGABlocks.getBlocks()) {
            add(block.block(), block.getEnglishName());
        }

        for (var translation : MEGATranslations.values()) {
            add(translation.getTranslationKey(), translation.getEnglishText());
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "Language";
    }
}

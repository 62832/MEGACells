package gripe._90.megacells.datagen;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;

public class CommonLanguageProvider implements DataProvider {
    private final Map<String, String> translationEntries = new TreeMap<>();
    private final PackOutput output;

    public CommonLanguageProvider(PackOutput output) {
        this.output = output;
    }

    private void addTranslations() {
        MEGAItems.getItems().forEach(this::add);
        MEGABlocks.getBlocks().forEach(this::add);
        Arrays.stream(MEGATranslations.values()).forEach(this::add);
        add("text.autoconfig.%s.title".formatted(MEGACells.MODID), MEGATranslations.ModName.getEnglishText());
    }

    private void add(ItemDefinition<?> item) {
        add(item.asItem().getDescriptionId(), item.getEnglishName());
    }

    private void add(BlockDefinition<?> block) {
        add(block.block().getDescriptionId(), block.getEnglishName());
    }

    private void add(MEGATranslations translation) {
        add(translation.getTranslationKey(), translation.getEnglishText());
    }

    private void add(String translationKey, String englishText) {
        if (translationEntries.containsKey(translationKey)) {
            throw new RuntimeException("Duplicate translation key: " + translationKey);
        }

        translationEntries.put(translationKey, englishText);
    }

    @NotNull
    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        var translationJson = new JsonObject();
        var translationFile = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang")
                .json(MEGACells.makeId("en_us"));

        addTranslations();
        translationEntries.forEach(translationJson::addProperty);
        return DataProvider.saveStable(writer, translationJson, translationFile);
    }

    @NotNull
    @Override
    public String getName() {
        return "Language";
    }
}

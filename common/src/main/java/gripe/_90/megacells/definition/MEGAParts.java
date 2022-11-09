package gripe._90.megacells.definition;

import java.util.function.Function;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

import gripe._90.megacells.part.MEGAPatternProviderPart;

public class MEGAParts {

    public static void init() {
        // controls static load order
    }

    // spotless:off
    public static final ItemDefinition<PartItem<MEGAPatternProviderPart>> MEGA_PATTERN_PROVIDER = part("MEGA Pattern Provider", "cable_mega_pattern_provider", MEGAPatternProviderPart.class, MEGAPatternProviderPart::new);
    // spotless:on

    private static <T extends IPart> ItemDefinition<PartItem<T>> part(String englishName, String id, Class<T> partClass,
            Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return MEGAItems.item(englishName, id, props -> new PartItem<>(props, partClass, factory));
    }
}

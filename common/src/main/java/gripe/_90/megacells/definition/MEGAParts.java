package gripe._90.megacells.definition;

import java.util.function.Function;

import net.minecraft.world.item.Item;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

import gripe._90.megacells.part.DecompressionModulePart;
import gripe._90.megacells.part.MEGAPatternProviderPart;

public final class MEGAParts {

    public static void init() {
        // controls static load order
    }

    // spotless:off
    public static final ItemDefinition<PartItem<MEGAPatternProviderPart>> MEGA_PATTERN_PROVIDER = customPart(
            "MEGA Pattern Provider",
            "cable_mega_pattern_provider",
            MEGAPatternProviderPart.class,
            MEGAPatternProviderPart.Item::new);
    public static final ItemDefinition<PartItem<DecompressionModulePart>> DECOMPRESSION_MODULE = part(
            "MEGA Decompression Module",
            "decompression_module",
            DecompressionModulePart.class,
            DecompressionModulePart::new);
    // spotless:on

    private static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        return customPart(englishName, id, partClass, props -> new PartItem<>(props, partClass, factory));
    }

    private static <T extends IPart> ItemDefinition<PartItem<T>> customPart(
            String englishName, String id, Class<T> partClass, Function<Item.Properties, PartItem<T>> itemFactory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return MEGAItems.item(englishName, id, itemFactory);
    }
}

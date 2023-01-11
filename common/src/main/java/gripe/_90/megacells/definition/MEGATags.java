package gripe._90.megacells.definition;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class MEGATags {
    public static final TagKey<Item> MEGA_PATTERN_PROVIDER = itemTag("megacells:mega_pattern_provider");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name));
    }
}

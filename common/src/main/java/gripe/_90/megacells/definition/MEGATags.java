package gripe._90.megacells.definition;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import gripe._90.megacells.MEGACells;

public final class MEGATags {
    public static final TagKey<Item> MEGA_PATTERN_PROVIDER = itemTag("mega_pattern_provider");
    public static final TagKey<Item> COMPRESSION_OVERRIDES = itemTag("compression_overrides");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, MEGACells.makeId(name));
    }
}

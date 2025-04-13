package gripe._90.megacells.definition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class MEGATags {
    public static final TagKey<Item> SKY_STEEL_INGOT = ItemTags.create(ResourceLocation.parse("c:ingots/sky_steel"));
    public static final TagKey<Item> SKY_BRONZE_INGOT = ItemTags.create(ResourceLocation.parse("c:ingots/sky_bronze"));
    public static final TagKey<Item> SKY_OSMIUM_INGOT = ItemTags.create(ResourceLocation.parse("c:ingots/sky_osmium"));

    public static final TagKey<Block> SKY_STEEL_BLOCK =
            BlockTags.create(ResourceLocation.parse("c:storage_blocks/sky_steel"));
    public static final TagKey<Block> SKY_BRONZE_BLOCK =
            BlockTags.create(ResourceLocation.parse("c:storage_blocks/sky_bronze"));
    public static final TagKey<Block> SKY_OSMIUM_BLOCK =
            BlockTags.create(ResourceLocation.parse("c:storage_blocks/sky_osmium"));

    public static final TagKey<Item> MEGA_INTERFACE = ItemTags.create(MEGABlocks.MEGA_INTERFACE.id());
    public static final TagKey<Item> MEGA_PATTERN_PROVIDER = ItemTags.create(MEGABlocks.MEGA_PATTERN_PROVIDER.id());
}

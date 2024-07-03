package gripe._90.megacells.definition;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import gripe._90.megacells.MEGACells;

public final class MEGATags {
    public static final TagKey<Item> SKY_STEEL_INGOT =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("forge:ingots/sky_steel"));

    private static final ResourceLocation SKY_STEEL_BLOCK_TAG =
            ResourceLocation.parse("forge:storage_blocks/sky_steel");
    public static final TagKey<Block> SKY_STEEL_BLOCK = TagKey.create(Registries.BLOCK, SKY_STEEL_BLOCK_TAG);
    public static final TagKey<Item> SKY_STEEL_BLOCK_ITEM = TagKey.create(Registries.ITEM, SKY_STEEL_BLOCK_TAG);

    public static final TagKey<Item> MEGA_INTERFACE = TagKey.create(Registries.ITEM, MEGABlocks.MEGA_INTERFACE.id());
    public static final TagKey<Item> MEGA_PATTERN_PROVIDER =
            TagKey.create(Registries.ITEM, MEGABlocks.MEGA_PATTERN_PROVIDER.id());

    public static final TagKey<Item> COMPRESSION_OVERRIDES =
            TagKey.create(Registries.ITEM, MEGACells.makeId("compression_overrides"));
}

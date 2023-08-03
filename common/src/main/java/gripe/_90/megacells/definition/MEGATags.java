package gripe._90.megacells.definition;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import gripe._90.megacells.util.Utils;

public final class MEGATags {
    public static final TagKey<Item> SKY_STEEL_INGOT = itemTag(
            switch (Utils.PLATFORM.getLoader()) {
                case FABRIC -> "c:sky_steel_ingots";
                case FORGE -> "forge:ingots/sky_steel";
            });

    private static final String SKY_STEEL_BLOCK_TAG =
            switch (Utils.PLATFORM.getLoader()) {
                case FABRIC -> "c:sky_steel_blocks";
                case FORGE -> "forge:storage_blocks/sky_steel";
            };
    public static final TagKey<Block> SKY_STEEL_BLOCK = blockTag(SKY_STEEL_BLOCK_TAG);
    public static final TagKey<Item> SKY_STEEL_BLOCK_ITEM = itemTag(SKY_STEEL_BLOCK_TAG);

    public static final TagKey<Item> MEGA_PATTERN_PROVIDER = itemTag("megacells:mega_pattern_provider");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(name));
    }

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(name));
    }
}

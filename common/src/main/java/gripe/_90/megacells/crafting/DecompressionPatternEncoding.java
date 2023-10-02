package gripe._90.megacells.crafting;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.util.CompressionVariant;

public class DecompressionPatternEncoding {
    private static final String NBT_BASE = "base";
    private static final String NBT_VARIANT = "variant";
    private static final String NBT_FACTOR = "factor";
    private static final String NBT_TO_COMPRESS = "toCompress";

    public static AEItemKey getBase(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a base tag.");
        return AEItemKey.fromTag(nbt.getCompound(NBT_BASE));
    }

    public static AEItemKey getVariant(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a variant tag.");
        return AEItemKey.fromTag(nbt.getCompound(NBT_VARIANT));
    }

    public static int getFactor(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a factor tag.");
        return nbt.getInt(NBT_FACTOR);
    }

    public static boolean getToCompress(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a toCompress tag.");
        return nbt.getBoolean(NBT_TO_COMPRESS);
    }

    public static void encode(CompoundTag tag, AEItemKey base, CompressionVariant variant, boolean toCompress) {
        tag.put(NBT_VARIANT, variant.item().toTag());
        tag.put(NBT_BASE, base.toTag());
        tag.putInt(NBT_FACTOR, variant.factor());
        tag.putBoolean(NBT_TO_COMPRESS, toCompress);
    }
}

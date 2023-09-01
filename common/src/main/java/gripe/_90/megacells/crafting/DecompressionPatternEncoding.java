package gripe._90.megacells.crafting;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

import appeng.api.stacks.AEItemKey;

public class DecompressionPatternEncoding {
    private static final String NBT_COMPRESSED = "compressed";
    private static final String NBT_DECOMPRESSED = "decompressed";
    private static final String NBT_COUNT = "count";
    private static final String NBT_FACTOR = "factor";

    public static AEItemKey getCompressed(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a compressed tag.");
        return AEItemKey.fromTag(nbt.getCompound(NBT_COMPRESSED));
    }

    public static AEItemKey getDecompressed(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a decompressed tag.");
        return AEItemKey.fromTag(nbt.getCompound(NBT_DECOMPRESSED));
    }

    public static long getCount(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a count tag.");
        return nbt.getLong(NBT_COUNT);
    }

    public static int getFactor(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a factor tag.");
        return nbt.getInt(NBT_FACTOR);
    }

    public static void encode(CompoundTag tag, AEItemKey compressed, AEItemKey decompressed, long count, int factor) {
        tag.put(NBT_COMPRESSED, compressed.toTag());
        tag.put(NBT_DECOMPRESSED, decompressed.toTag());
        tag.putLong(NBT_COUNT, count);
        tag.putInt(NBT_FACTOR, factor);
    }
}

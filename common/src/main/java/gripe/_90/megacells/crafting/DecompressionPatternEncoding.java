package gripe._90.megacells.crafting;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

import appeng.api.stacks.AEItemKey;

public class DecompressionPatternEncoding {
    private static final String NBT_COMPRESSED = "compressed";
    private static final String NBT_DECOMPRESSED = "decompressed";
    private static final String NBT_FACTOR = "factor";
    private static final String NBT_TO_COMPRESS = "toCompress";

    public static AEItemKey getCompressed(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a compressed tag.");
        return AEItemKey.fromTag(nbt.getCompound(NBT_COMPRESSED));
    }

    public static AEItemKey getDecompressed(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a decompressed tag.");
        return AEItemKey.fromTag(nbt.getCompound(NBT_DECOMPRESSED));
    }

    public static int getFactor(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a factor tag.");
        return nbt.getInt(NBT_FACTOR);
    }

    public static boolean getToCompress(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "Pattern must have a toCompress tag.");
        return nbt.getBoolean(NBT_TO_COMPRESS);
    }

    public static void encode(
            CompoundTag tag, AEItemKey compressed, AEItemKey decompressed, int factor, boolean toCompress) {
        tag.put(NBT_COMPRESSED, compressed.toTag());
        tag.put(NBT_DECOMPRESSED, decompressed.toTag());
        tag.putInt(NBT_FACTOR, factor);
        tag.putBoolean(NBT_TO_COMPRESS, toCompress);
    }
}

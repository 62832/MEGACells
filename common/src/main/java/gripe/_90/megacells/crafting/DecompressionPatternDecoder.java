package gripe._90.megacells.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.stacks.AEItemKey;
import appeng.core.AELog;

public class DecompressionPatternDecoder implements IPatternDetailsDecoder {
    public static final DecompressionPatternDecoder INSTANCE = new DecompressionPatternDecoder();

    private DecompressionPatternDecoder() {
    }

    @Override
    public boolean isEncodedPattern(ItemStack stack) {
        return stack.getItem() instanceof MEGADecompressionPattern.Item;
    }

    @Override
    public MEGADecompressionPattern decodePattern(AEItemKey what, Level level) {
        if (level == null || !(what.getItem() instanceof MEGADecompressionPattern.Item) || !(what.hasTag())) {
            return null;
        }

        try {
            return new MEGADecompressionPattern(what);
        } catch (Exception e) {
            AELog.warn("Could not decode an invalid decompression pattern %s: %s", what.getTag(), e);
            return null;
        }
    }

    @Override
    public MEGADecompressionPattern decodePattern(ItemStack what, Level level, boolean tryRecovery) {
        return decodePattern(AEItemKey.of(what), level);
    }
}
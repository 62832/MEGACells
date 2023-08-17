package gripe._90.megacells.crafting;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.EncodedPatternItem;

import gripe._90.megacells.MEGACells;

public class DecompressionPatternItem extends EncodedPatternItem {
    public DecompressionPatternItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public DecompressionPattern decode(ItemStack stack, Level level, boolean tryRecovery) {
        return decode(AEItemKey.of(stack), level);
    }

    @Nullable
    @Override
    public DecompressionPattern decode(AEItemKey what, Level level) {
        if (what == null || !(what.hasTag())) {
            return null;
        }

        try {
            return new DecompressionPattern(what);
        } catch (Exception e) {
            MEGACells.LOGGER.warn(
                    "Could not decode an invalid decompression pattern %s: %s".formatted(what.getTag(), e));
            return null;
        }
    }
}

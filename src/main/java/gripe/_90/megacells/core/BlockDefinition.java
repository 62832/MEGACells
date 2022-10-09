package gripe._90.megacells.core;

import java.util.Objects;

import com.google.common.base.Preconditions;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockDefinition<T extends Block> extends ItemDefinition<BlockItem> {

    private final T block;

    public BlockDefinition(ResourceLocation id, T block, BlockItem item) {
        super(id, item);
        this.block = Objects.requireNonNull(block, "block");
    }

    public final @NotNull T asBlock() {
        return this.block;
    }

    public final ItemStack stack(int stackSize) {
        Preconditions.checkArgument(stackSize > 0);
        return new ItemStack(block, stackSize);
    }
}

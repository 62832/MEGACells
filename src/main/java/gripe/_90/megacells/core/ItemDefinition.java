package gripe._90.megacells.core;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class ItemDefinition<T extends Item> implements ItemLike {

    private final ResourceLocation id;
    private final T item;

    public ItemDefinition(ResourceLocation id, T item) {
        Objects.requireNonNull(id);
        this.id = id;
        this.item = item;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public final @NotNull T asItem() {
        return this.item;
    }
}

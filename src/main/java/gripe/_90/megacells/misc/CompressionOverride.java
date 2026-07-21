package gripe._90.megacells.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record CompressionOverride(ItemStack larger, ItemStack smaller) {
    public static final Codec<Item> CODEC = Codec.STRING
            .comapFlatMap(
                    value -> {
                        if (value.equals("NONE")) {
                            return DataResult.success(Items.AIR);
                        }

                        var id = Identifier.tryParse(value);
                        if (id == null) {
                            return DataResult.error(() -> "Invalid override variant item ID: " + value);
                        }

                        return BuiltInRegistries.ITEM
                                .getOptional(id)
                                .map(DataResult::success)
                                .orElseGet(
                                        () -> DataResult.error(() -> "Could not find override variant item: " + value));
                    },
                    item -> item == Items.AIR
                            ? "NONE"
                            : BuiltInRegistries.ITEM.getKey(item).toString())
            .fieldOf("variant")
            .codec();

    @NotNull
    public String toString() {
        return String.format(
                "%s → %dx %s",
                CompressionService.variantString(larger),
                smaller.getCount(),
                CompressionService.variantString(smaller));
    }
}

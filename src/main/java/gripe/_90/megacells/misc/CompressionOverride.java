package gripe._90.megacells.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record CompressionOverride(ItemStack larger, ItemStack smaller) {
    // TODO: Extend further with support for specific amounts and components, to allow for e.g. compression variants
    //  from recipes other than conventional crafting altogether
    public static final Codec<Item> CODEC = Codec.STRING
            .comapFlatMap(
                    str -> {
                        if (str.equals("NONE")) {
                            return DataResult.success(Items.AIR);
                        } else {
                            try {
                                var id = ResourceLocation.parse(str);
                                var item = BuiltInRegistries.ITEM.get(id);
                                return item == Items.AIR && id != BuiltInRegistries.ITEM.getKey(Items.AIR)
                                        ? DataResult.error(() -> "Could not find override variant item: " + str)
                                        : DataResult.success(item);
                            } catch (ResourceLocationException e) {
                                return DataResult.error(e::getMessage);
                            }
                        }
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

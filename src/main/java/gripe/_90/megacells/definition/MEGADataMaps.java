package gripe._90.megacells.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import gripe._90.megacells.MEGACells;

public final class MEGADataMaps {
    private static final Codec<Item> COMPRESSION_VARIANT_CODEC = Codec.STRING
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

    public static final DataMapType<Item, Item> COMPRESSION_OVERRIDE = DataMapType.builder(
                    MEGACells.makeId("compression_overrides"), Registries.ITEM, COMPRESSION_VARIANT_CODEC)
            .build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(COMPRESSION_OVERRIDE);
    }
}

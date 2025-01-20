package gripe._90.megacells.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import gripe._90.megacells.MEGACells;

public record CompressionOverride(Item variant) {
    private static final Codec<CompressionOverride> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("variant").forGetter(CompressionOverride::variant))
            .apply(instance, CompressionOverride::new));

    public static final DataMapType<Item, CompressionOverride> DATA = DataMapType.builder(
                    MEGACells.makeId("compression_overrides"), Registries.ITEM, CODEC)
            .build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(DATA);
    }
}

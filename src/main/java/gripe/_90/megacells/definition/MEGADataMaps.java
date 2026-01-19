package gripe._90.megacells.definition;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.misc.CompressionOverride;

public final class MEGADataMaps {
    public static final DataMapType<Item, Item> COMPRESSION_OVERRIDE = DataMapType.builder(
                    MEGACells.makeId("compression_overrides"), Registries.ITEM, CompressionOverride.CODEC)
            .build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(COMPRESSION_OVERRIDE);
    }
}

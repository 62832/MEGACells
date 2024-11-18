package gripe._90.megacells.definition;

import java.math.BigInteger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.stacks.AEKey;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.misc.DecompressionPattern;

import io.netty.buffer.ByteBuf;

public final class MEGAComponents {
    public static final DeferredRegister<DataComponentType<?>> DR =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MEGACells.MODID);

    public static final DataComponentType<AEKey> BULK_CELL_ITEM =
            register("bulk_item", AEKey.CODEC, AEKey.STREAM_CODEC);
    public static final DataComponentType<Integer> BULK_CELL_COMPRESSION_CUTOFF =
            register("compression_cutoff", Codec.INT, ByteBufCodecs.VAR_INT);

    private static final Codec<BigInteger> BIG_INTEGER_CODEC = Codec.STRING.comapFlatMap(
            str -> {
                try {
                    return DataResult.success(new BigInteger(str));
                } catch (NumberFormatException e) {
                    return DataResult.error(e::getMessage);
                }
            },
            BigInteger::toString);

    private static final StreamCodec<ByteBuf, BigInteger> BIG_INTEGER_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
            str -> {
                try {
                    return new BigInteger(str);
                } catch (NumberFormatException e) {
                    return BigInteger.ZERO;
                }
            },
            BigInteger::toString);

    public static final DataComponentType<BigInteger> BULK_CELL_UNIT_COUNT =
            register("bulk_unit_count", BIG_INTEGER_CODEC, BIG_INTEGER_STREAM_CODEC);

    public static final DataComponentType<BigInteger> BULK_CELL_UNIT_FACTOR =
            register("bulk_unit_factor", BIG_INTEGER_CODEC, BIG_INTEGER_STREAM_CODEC);

    public static final DataComponentType<DecompressionPattern.Encoded> ENCODED_DECOMPRESSION_PATTERN = register(
            "encoded_decompression_pattern",
            DecompressionPattern.Encoded.CODEC,
            DecompressionPattern.Encoded.STREAM_CODEC);

    public static final DataComponentType<AEKey> RADIOACTIVE_CELL_CHEMICAL =
            register("radioactive_chemical", AEKey.CODEC, AEKey.STREAM_CODEC);

    public static final DataComponentType<Long> RADIOACTIVE_CELL_AMOUNT =
            register("radioactive_amount", Codec.LONG, ByteBufCodecs.VAR_LONG);

    private static <T> DataComponentType<T> register(
            String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        var componentType = DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
                .build();
        DR.register(name, () -> componentType);
        return componentType;
    }
}

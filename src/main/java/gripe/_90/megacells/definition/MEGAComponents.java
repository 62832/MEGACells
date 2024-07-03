package gripe._90.megacells.definition;

import java.math.BigInteger;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.misc.DecompressionPattern;

import io.netty.buffer.ByteBuf;

public final class MEGAComponents {
    public static final DeferredRegister<DataComponentType<?>> DEFERRED =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MEGACells.MODID);

    public static final DataComponentType<AEKey> BULK_CELL_ITEM = register(
            "bulk_item", builder -> builder.persistent(AEKey.CODEC).networkSynchronized(AEItemKey.STREAM_CODEC));

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
            register("bulk_unit_count", builder -> builder.persistent(BIG_INTEGER_CODEC)
                    .networkSynchronized(BIG_INTEGER_STREAM_CODEC));

    public static final DataComponentType<BigInteger> BULK_CELL_UNIT_FACTOR =
            register("bulk_unit_factor", builder -> builder.persistent(BIG_INTEGER_CODEC)
                    .networkSynchronized(BIG_INTEGER_STREAM_CODEC));

    public static final DataComponentType<DecompressionPattern.Encoded> ENCODED_DECOMPRESSION_PATTERN =
            register("encoded_decompression_pattern", builder -> builder.persistent(DecompressionPattern.Encoded.CODEC)
                    .networkSynchronized(DecompressionPattern.Encoded.STREAM_CODEC));

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DEFERRED.register(name, () -> componentType);
        return componentType;
    }
}

package gripe._90.megacells.misc;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import gripe._90.megacells.MEGACells;

import io.netty.buffer.ByteBuf;

public record SyncCompressionChainsPacket(Set<CompressionChain> chains) implements CustomPacketPayload {
    public static final Type<SyncCompressionChainsPacket> TYPE = new Type<>(MEGACells.makeId("get_compression_chain"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCompressionChainsPacket> STREAM_CODEC =
            CompressionChain.STREAM_CODEC
                    .apply(set())
                    .map(SyncCompressionChainsPacket::new, SyncCompressionChainsPacket::chains);

    private static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> set() {
        return codec -> ByteBufCodecs.collection(HashSet::new, codec);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

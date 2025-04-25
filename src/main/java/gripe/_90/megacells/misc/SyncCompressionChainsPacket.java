package gripe._90.megacells.misc;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import gripe._90.megacells.MEGACells;

public record SyncCompressionChainsPacket(List<CompressionChain> chains) implements CustomPacketPayload {
    public static final Type<SyncCompressionChainsPacket> TYPE = new Type<>(MEGACells.makeId("get_compression_chain"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCompressionChainsPacket> STREAM_CODEC =
            CompressionChain.STREAM_CODEC
                    .apply(ByteBufCodecs.list())
                    .map(SyncCompressionChainsPacket::new, SyncCompressionChainsPacket::chains);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

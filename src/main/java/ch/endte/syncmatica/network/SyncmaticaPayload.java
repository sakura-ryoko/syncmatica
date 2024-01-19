package ch.endte.syncmatica.network;

import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.PacketByteBuf; --> FriendlyByteBuf
import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.packet.CustomPayload; --> CustomPacketPayload
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.util.Identifier; --> ResourceLocation
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SyncmaticaPayload(CompoundTag data) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SyncmaticaPayload> STREAM_CODEC = CustomPacketPayload.codec(SyncmaticaPayload::write, SyncmaticaPayload::new);
    public static final ResourceLocation SYNCMATICA_CHANNEL = new ResourceLocation("syncmatica", "hello");
    public static final Type<SyncmaticaPayload> TYPE = new CustomPacketPayload.Type<>(SYNCMATICA_CHANNEL);
    /*public SyncmaticaPayload(ResourceLocation identifier, FriendlyByteBuf input) {
        this(new FriendlyByteBuf(input.readBytes(input.readableBytes())), identifier);
    }*/

    public SyncmaticaPayload(FriendlyByteBuf input) {
        this(input.readNbt());
    }

    //@Override
    public void write(FriendlyByteBuf output) {
        output.writeNbt(data);
    }
    @Override
    public @NotNull Type<SyncmaticaPayload> type() {
        return TYPE;
    }
}

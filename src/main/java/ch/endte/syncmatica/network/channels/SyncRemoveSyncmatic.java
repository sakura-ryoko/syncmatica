package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncRemoveSyncmatic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncRemoveSyncmatic> TYPE = new Id<>(PacketType.REMOVE_SYNCMATIC.getId());
    public static final PacketCodec<PacketByteBuf, SyncRemoveSyncmatic> CODEC = CustomPayload.codecOf(SyncRemoveSyncmatic::write, SyncRemoveSyncmatic::new);

    public SyncRemoveSyncmatic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

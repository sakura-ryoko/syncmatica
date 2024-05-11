package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncModifyRequestAccept(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncModifyRequestAccept> TYPE = new Id<>(PacketType.MODIFY_REQUEST_ACCEPT.getId());
    public static final PacketCodec<PacketByteBuf, SyncModifyRequestAccept> CODEC = CustomPayload.codecOf(SyncModifyRequestAccept::write, SyncModifyRequestAccept::new);

    public SyncModifyRequestAccept(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

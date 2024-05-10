package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncModifyRequestAccept(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncModifyRequestAccept> TYPE = new Id<>(new Identifier("syncmatica", "modify_request_accept"));
    public static final PacketCodec<PacketByteBuf, SyncModifyRequestAccept> CODEC = CustomPayload.codecOf(SyncModifyRequestAccept::write, SyncModifyRequestAccept::new);

    public SyncModifyRequestAccept(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

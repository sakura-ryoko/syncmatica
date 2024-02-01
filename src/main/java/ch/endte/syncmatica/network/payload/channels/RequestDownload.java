package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestDownload(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<RequestDownload> TYPE = new Id<>(new Identifier("syncmatica", "request_download"));
    public static final PacketCodec<PacketByteBuf, RequestDownload> CODEC = CustomPayload.codecOf(RequestDownload::write, RequestDownload::new);

    public RequestDownload(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

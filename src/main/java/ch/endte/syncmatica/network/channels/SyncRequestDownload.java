package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncRequestDownload(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncRequestDownload> TYPE = new Id<>(new Identifier("syncmatica", "request_download"));
    public static final PacketCodec<PacketByteBuf, SyncRequestDownload> CODEC = CustomPayload.codecOf(SyncRequestDownload::write, SyncRequestDownload::new);

    public SyncRequestDownload(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

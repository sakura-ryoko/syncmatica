package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncSendLitematic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncSendLitematic> TYPE = new Id<>(PacketType.SEND_LITEMATIC.getId());
    public static final PacketCodec<PacketByteBuf, SyncSendLitematic> CODEC = CustomPayload.codecOf(SyncSendLitematic::write, SyncSendLitematic::new);

    public SyncSendLitematic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

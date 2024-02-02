package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncSendLitematic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncSendLitematic> TYPE = new Id<>(new Identifier("syncmatica", "send_litematic"));
    public static final PacketCodec<PacketByteBuf, SyncSendLitematic> CODEC = CustomPayload.codecOf(SyncSendLitematic::write, SyncSendLitematic::new);

    public SyncSendLitematic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

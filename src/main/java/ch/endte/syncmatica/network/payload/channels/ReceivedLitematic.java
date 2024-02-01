package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ReceivedLitematic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<ReceivedLitematic> TYPE = new Id<>(new Identifier("syncmatica", "received_litematic"));
    public static final PacketCodec<PacketByteBuf, ReceivedLitematic> CODEC = CustomPayload.codecOf(ReceivedLitematic::write, ReceivedLitematic::new);

    public ReceivedLitematic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

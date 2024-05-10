package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncReceivedLitematic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncReceivedLitematic> TYPE = new Id<>(new Identifier("syncmatica", "received_litematic"));
    public static final PacketCodec<PacketByteBuf, SyncReceivedLitematic> CODEC = CustomPayload.codecOf(SyncReceivedLitematic::write, SyncReceivedLitematic::new);

    public SyncReceivedLitematic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

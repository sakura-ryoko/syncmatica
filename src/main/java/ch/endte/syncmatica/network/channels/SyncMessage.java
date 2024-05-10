package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncMessage(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncMessage> TYPE = new Id<>(new Identifier("syncmatica", "message"));
    public static final PacketCodec<PacketByteBuf, SyncMessage> CODEC = CustomPayload.codecOf(SyncMessage::write, SyncMessage::new);

    public SyncMessage(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

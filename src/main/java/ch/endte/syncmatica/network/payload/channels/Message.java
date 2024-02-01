package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record Message(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<Message> TYPE = new Id<>(new Identifier("syncmatica", "message"));
    public static final PacketCodec<PacketByteBuf, Message> CODEC = CustomPayload.codecOf(Message::write, Message::new);

    public Message(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncModify(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncModify> TYPE = new Id<>(new Identifier("syncmatica", "modify"));
    public static final PacketCodec<PacketByteBuf, SyncModify> CODEC = CustomPayload.codecOf(SyncModify::write, SyncModify::new);

    public SyncModify(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

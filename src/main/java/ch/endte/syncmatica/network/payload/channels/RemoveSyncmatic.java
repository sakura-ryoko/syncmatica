package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RemoveSyncmatic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<RemoveSyncmatic> TYPE = new Id<>(new Identifier("syncmatica", "remove_syncmatic"));
    public static final PacketCodec<PacketByteBuf, RemoveSyncmatic> CODEC = CustomPayload.codecOf(RemoveSyncmatic::write, RemoveSyncmatic::new);

    public RemoveSyncmatic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

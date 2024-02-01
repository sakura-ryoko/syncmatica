package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CancelShare(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<CancelShare> TYPE = new Id<>(new Identifier("syncmatica", "cancel_share"));
    public static final PacketCodec<PacketByteBuf, CancelShare> CODEC = CustomPayload.codecOf(CancelShare::write, CancelShare::new);

    public CancelShare(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncCancelShare(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncCancelShare> TYPE = new Id<>(new Identifier("syncmatica", "cancel_share"));
    public static final PacketCodec<PacketByteBuf, SyncCancelShare> CODEC = CustomPayload.codecOf(SyncCancelShare::write, SyncCancelShare::new);

    public SyncCancelShare(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

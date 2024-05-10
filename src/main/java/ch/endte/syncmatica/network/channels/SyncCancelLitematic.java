package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncCancelLitematic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncCancelLitematic> TYPE = new Id<>(new Identifier("syncmatica", "cancel_litematic"));
    public static final PacketCodec<PacketByteBuf, SyncCancelLitematic> CODEC = CustomPayload.codecOf(SyncCancelLitematic::write, SyncCancelLitematic::new);

    public SyncCancelLitematic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

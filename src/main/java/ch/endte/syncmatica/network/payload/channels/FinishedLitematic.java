package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FinishedLitematic(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<FinishedLitematic> TYPE = new Id<>(new Identifier("syncmatica", "finished_litematic"));
    public static final PacketCodec<PacketByteBuf, FinishedLitematic> CODEC = CustomPayload.codecOf(FinishedLitematic::write, FinishedLitematic::new);

    public FinishedLitematic(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

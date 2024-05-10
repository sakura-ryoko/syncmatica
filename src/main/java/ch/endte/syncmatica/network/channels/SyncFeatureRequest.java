package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncFeatureRequest(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncFeatureRequest> TYPE = new Id<>(new Identifier("syncmatica", "feature_request"));
    public static final PacketCodec<PacketByteBuf, SyncFeatureRequest> CODEC = CustomPayload.codecOf(SyncFeatureRequest::write, SyncFeatureRequest::new);

    public SyncFeatureRequest(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

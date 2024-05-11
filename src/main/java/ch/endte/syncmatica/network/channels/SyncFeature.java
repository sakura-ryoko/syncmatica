package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncFeature(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncFeature> TYPE = new Id<>(PacketType.FEATURE.getId());
    public static final PacketCodec<PacketByteBuf, SyncFeature> CODEC = CustomPayload.codecOf(SyncFeature::write, SyncFeature::new);

    public SyncFeature(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

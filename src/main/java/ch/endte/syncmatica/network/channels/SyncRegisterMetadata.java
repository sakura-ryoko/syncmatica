package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncRegisterMetadata(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncRegisterMetadata> TYPE = new Id<>(PacketType.REGISTER_METADATA.getId());
    public static final PacketCodec<PacketByteBuf, SyncRegisterMetadata> CODEC = CustomPayload.codecOf(SyncRegisterMetadata::write, SyncRegisterMetadata::new);

    public SyncRegisterMetadata(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}

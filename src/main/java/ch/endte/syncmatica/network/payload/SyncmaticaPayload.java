package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.Syncmatica;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncmaticaPayload(SyncData data) implements CustomPayload
{
    public static final Id<SyncmaticaPayload> TYPE = new Id<>(Syncmatica.NETWORK_ID);
    public static final PacketCodec<PacketByteBuf, SyncmaticaPayload> CODEC = CustomPayload.codecOf(SyncmaticaPayload::write, SyncmaticaPayload::new);

    public SyncmaticaPayload(PacketByteBuf input)
    {
        this(SyncData.fromPacket(input));
    }

    private void write(PacketByteBuf output)
    {
        data.toPacket(output);
    }

    @Override
    public Id<SyncmaticaPayload> getId()
    {
        return TYPE;
    }
}

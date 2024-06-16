package ch.endte.syncmatica.network;

import javax.annotation.Nonnull;
import ch.endte.syncmatica.Syncmatica;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SyncmaticaPacket
{
    private final PacketByteBuf packet;
    private final PacketType type;
    private final Identifier channel;

    public SyncmaticaPacket(@Nonnull Identifier channel, @Nonnull PacketByteBuf packet)
    {
        this.channel = channel;
        this.packet = packet;
        this.type = PacketType.getType(channel);
    }

    public PacketType getType()
    {
        return this.type;
    }

    public Identifier getChannel()
    {
        return this.channel;
    }

    public PacketByteBuf getPacket()
    {
        return new PacketByteBuf(this.packet);
    }

    protected static SyncmaticaPacket fromPacket(PacketByteBuf input)
    {
        return new SyncmaticaPacket(input.readIdentifier(), new PacketByteBuf(input.readBytes(input.readableBytes())));
    }

    protected void toPacket(PacketByteBuf output)
    {
        output.writeIdentifier(this.channel);
        output.writeBytes(this.packet.readBytes(this.packet.readableBytes()));
    }

    public record Payload(SyncmaticaPacket data) implements CustomPayload
    {
        public static final Id<Payload> ID = new Id<>(Syncmatica.NETWORK_ID);
        public static final PacketCodec<PacketByteBuf, Payload> CODEC = CustomPayload.codecOf(Payload::write, Payload::new);

        public Payload(PacketByteBuf input)
        {
            this(SyncmaticaPacket.fromPacket(input));
        }

        private void write(PacketByteBuf output)
        {
            data.toPacket(output);
        }

        @Override
        public Id<Payload> getId()
        {
            return ID;
        }
    }
}

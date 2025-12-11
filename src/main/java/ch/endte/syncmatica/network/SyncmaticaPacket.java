package ch.endte.syncmatica.network;

import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import ch.endte.syncmatica.Syncmatica;
import org.jspecify.annotations.NonNull;

public class SyncmaticaPacket
{
    private final FriendlyByteBuf packet;
    private final PacketType type;
    private final Identifier channel;

    public SyncmaticaPacket(@Nonnull Identifier channel, @Nonnull FriendlyByteBuf packet)
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

    public FriendlyByteBuf getPacket()
    {
        return this.packet;
    }

    protected static SyncmaticaPacket fromPacket(FriendlyByteBuf input)
    {
        return new SyncmaticaPacket(input.readIdentifier(), new FriendlyByteBuf(input.readBytes(input.readableBytes())));
    }

    protected void toPacket(FriendlyByteBuf output)
    {
        output.writeIdentifier(this.channel);
        output.writeBytes(this.packet.copy());
    }

    public record Payload(SyncmaticaPacket data) implements CustomPacketPayload
    {
        public static final Type<Payload> ID = new Type<>(Syncmatica.NETWORK_ID);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(Payload::write, Payload::new);

        public Payload(FriendlyByteBuf input)
        {
            this(SyncmaticaPacket.fromPacket(input));
        }

        private void write(FriendlyByteBuf output)
        {
            data.toPacket(output);
        }

        @Override
        public @NonNull Type<Payload> type()
        {
            return ID;
        }
    }
}

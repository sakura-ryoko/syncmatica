package ch.endte.syncmatica.network;

import javax.annotation.Nonnull;
import ch.endte.syncmatica.Syncmatica;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.lang.ref.Cleaner;

public class SyncmaticaPacket
{
    private static final Cleaner cleaner = Cleaner.create();

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
        return this.packet;
    }

    protected static SyncmaticaPacket fromPacket(PacketByteBuf input)
    {
        // hardly to know its life cycle, so we still leave it to GC
        return new SyncmaticaPacket(input.readIdentifier(), new GCPacketByteBuf(input.readBytes(input.readableBytes())));
    }

    protected void toPacket(PacketByteBuf output)
    {
        ByteBuf buf = this.packet.copy();

        output.writeIdentifier(this.channel);
        output.writeBytes(buf);  // use copy directly because we don't need to move the reader index

        buf.release();  // release it otherwise it will cause memory leak
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

    protected static class GCPacketByteBuf extends PacketByteBuf
    {
        public GCPacketByteBuf(ByteBuf buf)
        {
            super(buf);
            cleaner.register(this, buf::release);
        }
    }
}

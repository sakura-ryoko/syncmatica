package ch.endte.syncmatica.network.payload;

import javax.annotation.Nonnull;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncData
{
    private final PacketByteBuf packet;
    private final PacketType type;
    private final Identifier channel;

    public SyncData(@Nonnull Identifier channel, @Nonnull PacketByteBuf packet)
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

    protected static SyncData fromPacket(PacketByteBuf input)
    {
        return new SyncData(input.readIdentifier(), new PacketByteBuf(input.readBytes(input.readableBytes())));
    }

    protected void toPacket(PacketByteBuf output)
    {
        output.writeIdentifier(this.channel);
        output.writeBytes(this.packet.readBytes(this.packet.readableBytes()));
    }
}

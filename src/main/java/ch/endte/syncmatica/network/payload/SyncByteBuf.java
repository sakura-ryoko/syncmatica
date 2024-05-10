package ch.endte.syncmatica.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;

/**
 * Hacky way to get around the errors not being able to create a CustomPayload based on a PacketByteBuf directly
 * Create your own that extends it... xD
 */
public class SyncByteBuf extends PacketByteBuf
{
    public SyncByteBuf(ByteBuf parent)
    {
        super(parent);
    }
}

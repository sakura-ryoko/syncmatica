package ch.endte.syncmatica.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;

/**
 * Hacky way to get around the errors not being able to create a CustomPayload based on a PacketByteBuf directly
 * Just create your own that extends it... xD
 */
public class SyncByteBuf extends PacketByteBuf
{
    // Passes all calls to PacketByteBuf
    public SyncByteBuf(ByteBuf parent)
    {
        super(parent);
    }
}

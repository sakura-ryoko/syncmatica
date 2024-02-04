package ch.endte.syncmatica.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;

/**
 * Hacky way to get around the errors not being able to create a CustomPayload based on a PacketByteBuf directly
 * Just create your own that extends it... xD
 * I tried adding the Identifier here, but failed to accomplish that successfully.
 */
public class SyncByteBuf extends PacketByteBuf
{
    // Passes calls to PacketByteBuf
    public SyncByteBuf(ByteBuf parent)
    {
        super(parent);
    }
}

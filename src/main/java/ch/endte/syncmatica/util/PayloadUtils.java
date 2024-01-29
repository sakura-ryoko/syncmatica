package fi.dy.masa.servux.util;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class PayloadUtils
{
    public static PacketByteBuf fromNbt(NbtCompound nbt, String key)
    {
        if (!nbt.isEmpty())
        {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByteArray(nbt.getByteArray(key));
            return buf;
        } else return null;
    }
    public static NbtCompound fromByteBuf(PacketByteBuf buf, String key)
    {
        if (buf.isReadable())
        {
            NbtCompound nbt = new NbtCompound();
            nbt.putByteArray(key, buf.readByteArray());
            return nbt;
        } else return null;
    }
}

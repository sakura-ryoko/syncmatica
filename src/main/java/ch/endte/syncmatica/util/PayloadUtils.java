package ch.endte.syncmatica.util;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.*;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

/**
 * Utility to convert NbtCompound into/from a PacketByteBuf
 */
public class PayloadUtils
{
    @Nullable
    public static PacketByteBuf fromNbt(NbtCompound nbt, String key)
    {
        if (!nbt.isEmpty())
        {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByteArray(nbt.getByteArray(key));
            return buf;
        }
        else return null;
    }
    @Nullable
    public static NbtCompound fromByteBuf(PacketByteBuf buf, String key)
    {
        if (buf.isReadable())
        {
            NbtCompound nbt = new NbtCompound();
            nbt.putByteArray(key, buf.readByteArray());
            return nbt;
        }
        else return null;
    }

    @Nullable
    public static <T extends CustomPayload> T getPayload(PacketType type, SyncByteBuf buf)
    {
        Object payload = null;
        if (type.equals(PacketType.CANCEL_SHARE))
        {
            payload = new CancelShare(buf);
        }
        else if (type.equals(PacketType.CONFIRM_USER))
        {
            payload = new ConfirmUser(buf);
        }
        else if (type.equals(PacketType.FEATURE))
        {
            payload = new Feature(buf);
        }
        else if (type.equals(PacketType.FEATURE_REQUEST))
        {
            payload = new FeatureRequest(buf);
        }
        else if (type.equals(PacketType.FINISHED_LITEMATIC))
        {
            payload = new FinishedLitematic(buf);
        }
        else if (type.equals(PacketType.MESSAGE))
        {
            payload = new Message(buf);
        }
        else if (type.equals(PacketType.MODIFY))
        {
            payload = new Modify(buf);
        }
        else if (type.equals(PacketType.MODIFY_FINISH))
        {
            payload = new ModifyFinish(buf);
        }
        else if (type.equals(PacketType.MODIFY_REQUEST))
        {
            payload = new ModifyRequest(buf);
        }
        else if (type.equals(PacketType.MODIFY_REQUEST_ACCEPT))
        {
            payload = new ModifyRequestAccept(buf);
        }
        else if (type.equals(PacketType.MODIFY_REQUEST_DENY))
        {
            payload = new ModifyRequestDeny(buf);
        }
        else if (type.equals(PacketType.RECEIVED_LITEMATIC))
        {
            payload = new ReceivedLitematic(buf);
        }
        else if (type.equals(PacketType.REGISTER_METADATA))
        {
            payload = new RegisterMetadata(buf);
        }
        else if (type.equals(PacketType.REGISTER_VERSION))
        {
            payload = new RegisterVersion(buf);
        }
        else if (type.equals(PacketType.REMOVE_SYNCMATIC))
        {
            payload = new RemoveSyncmatic(buf);
        }
        else if (type.equals(PacketType.REQUEST_LITEMATIC))
        {
            payload = new RequestDownload(buf);
        }
        else if (type.equals(PacketType.SEND_LITEMATIC))
        {
            payload = new SendLitematic(buf);
        }
        else
        {
            return null;
        }
        return (T) payload;
    }
    @Nullable
    public static <T extends CustomPayload> T getPayload(PacketType type, NbtCompound data)
    {
        if (type.equals(PacketType.NBT_DATA))
        {
            Object payload = new SyncmaticaNbtData(data);
            return (T) payload;
        }
        else return null;
    }
}

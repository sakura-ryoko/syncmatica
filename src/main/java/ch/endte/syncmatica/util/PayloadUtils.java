package ch.endte.syncmatica.util;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.channels.*;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

/**
 * Utility to convert NbtCompound into/from a PacketByteBuf,
 * And some Payload/PacketType-related calls
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

    public static PacketByteBuf fromSyncBuf(SyncByteBuf in)
    {
        return new PacketByteBuf(in.asByteBuf());
    }
    public static SyncByteBuf fromByteBuf(PacketByteBuf in)
    {
        return new SyncByteBuf(in.asByteBuf());
    }

    @Nullable
    public static CustomPayload getPayload(PacketType type, SyncByteBuf buf)
    {
        CustomPayload payload;
        if (type.equals(PacketType.CANCEL_SHARE))
        {
            payload = new SyncCancelShare(buf);
        }
        else if (type.equals(PacketType.CANCEL_LITEMATIC))
        {
            payload = new SyncCancelLitematic(buf);
        }
        else if (type.equals(PacketType.CONFIRM_USER))
        {
            payload = new SyncConfirmUser(buf);
        }
        else if (type.equals(PacketType.FEATURE))
        {
            payload = new SyncFeature(buf);
        }
        else if (type.equals(PacketType.FEATURE_REQUEST))
        {
            payload = new SyncFeatureRequest(buf);
        }
        else if (type.equals(PacketType.FINISHED_LITEMATIC))
        {
            payload = new SyncFinishedLitematic(buf);
        }
        else if (type.equals(PacketType.MESSAGE))
        {
            payload = new SyncMessage(buf);
        }
        else if (type.equals(PacketType.MODIFY))
        {
            payload = new SyncModify(buf);
        }
        else if (type.equals(PacketType.MODIFY_FINISH))
        {
            payload = new SyncModifyFinish(buf);
        }
        else if (type.equals(PacketType.MODIFY_REQUEST))
        {
            payload = new SyncModifyRequest(buf);
        }
        else if (type.equals(PacketType.MODIFY_REQUEST_ACCEPT))
        {
            payload = new SyncModifyRequestAccept(buf);
        }
        else if (type.equals(PacketType.MODIFY_REQUEST_DENY))
        {
            payload = new SyncModifyRequestDeny(buf);
        }
        else if (type.equals(PacketType.RECEIVED_LITEMATIC))
        {
            payload = new SyncReceivedLitematic(buf);
        }
        else if (type.equals(PacketType.REGISTER_METADATA))
        {
            payload = new SyncRegisterMetadata(buf);
        }
        else if (type.equals(PacketType.REGISTER_VERSION))
        {
            payload = new SyncRegisterVersion(buf);
        }
        else if (type.equals(PacketType.REMOVE_SYNCMATIC))
        {
            payload = new SyncRemoveSyncmatic(buf);
        }
        else if (type.equals(PacketType.REQUEST_LITEMATIC))
        {
            payload = new SyncRequestDownload(buf);
        }
        else if (type.equals(PacketType.SEND_LITEMATIC))
        {
            payload = new SyncSendLitematic(buf);
        }
        else
        {
            return null;
        }
        return payload;
    }

    @Nullable
    public static CustomPayload getPayload(PacketType type, NbtCompound data)
    {
        if (type.equals(PacketType.NBT_DATA))
        {
            return new SyncNbtData(data);
        }
        else return null;
    }
}

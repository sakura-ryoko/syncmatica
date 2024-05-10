package ch.endte.syncmatica.network.client;

import ch.endte.syncmatica.network.packet.ActorClientPlayHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.channels.*;
import ch.endte.syncmatica.util.PayloadUtils;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Main Fabric API Networking-based packet senders / receivers (Client Context)
 */
public class ClientPlayHandler
{
    public static <T extends CustomPayload> void sendSyncPacket(T payload)
    {
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
            SyncLog.debug("ClientPlayHandler#sendSyncPacket(): [API] sending payload id: {}", payload.getId().id().toString());
        }
        else
            SyncLog.warn("ClientPlayHandler#sendSyncPacket(): [API] can't send packet (not accepted).");
    }

    public static <T extends CustomPayload> void sendSyncPacket(T payload, ClientPlayNetworkHandler handler)
    {
        Packet<?> packet = new CustomPayloadC2SPacket(payload);
        if (handler.accepts(packet))
        {
            handler.sendPacket(packet);
            SyncLog.debug("ClientPlayHandler#sendSyncPacket(): [Handler] sending payload id: {}", payload.getId().id().toString());
        }
        else
            SyncLog.warn("ClientPlayHandler#sendSyncPacket(): [Handler] can't send packet (not accepted).");
    }

    public static void receiveSyncPacket(PacketType type, SyncByteBuf data, ClientPlayNetworkHandler handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        PacketByteBuf out = PayloadUtils.fromSyncBuf(data);
        SyncLog.debug("ClientPlayHandler#receiveSyncPacket(): received payload id: {}, size in bytes {}", type.getId().toString(), out.readableBytes());

        if (handler == null)
        {
            SyncLog.warn("ClientPlayHandler#receiveSyncPacket(): ignored because handler is null.");
        }
        else
        {
            ActorClientPlayHandler.getInstance().packetEvent(type, out, handler, ci);
        }
    }

    public static void receiveSyncNbt(PacketType type, NbtCompound data, ClientPlayNetworkHandler handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncNbt", false);
        SyncLog.debug("ClientPlayHandler#receiveSyncPacket(): received payload id: {}, size in bytes {}", type.getId().toString(), data.getSizeInBytes());
        SyncLog.debug("ClientPlayHandler#receiveSyncPacket(): payload.readString(): {}", data.getString(SyncNbtData.KEY));

        if (handler == null)
        {
            SyncLog.warn("ClientPlayHandler#receiveSyncPacket(): ignored because handler is null.");
        }
        else
        {
            ActorClientPlayHandler.getInstance().packetNbtEvent(type, data, handler, ci);
        }
    }

    public static void receiveCancelShare(SyncCancelShare data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_SHARE, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveCancelLitematic(SyncCancelLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveConfirmUser(SyncConfirmUser data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CONFIRM_USER, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveFeature(SyncFeature data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveFeatureRequest(SyncFeatureRequest data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE_REQUEST, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveFinishedLitematic(SyncFinishedLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FINISHED_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveMessage(SyncMessage data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MESSAGE, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModify(SyncModify data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyFinish(SyncModifyFinish data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_FINISH, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyRequest(SyncModifyRequest data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyRequestAccept(SyncModifyRequestAccept data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_ACCEPT, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyRequestDeny(SyncModifyRequestDeny data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_DENY, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveReceivedLitematic(SyncReceivedLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.RECEIVED_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveRegisterMetadata(SyncRegisterMetadata data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_METADATA, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveRegisterVersion(SyncRegisterVersion data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_VERSION, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveRemoveSyncmatic(SyncRemoveSyncmatic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REMOVE_SYNCMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }
    public static void receiveRequestDownload(SyncRequestDownload data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REQUEST_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveSendLitematic(SyncSendLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.SEND_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveSyncNbtData(SyncNbtData data, ClientPlayNetworking.Context context)
    {
        receiveSyncNbt(PacketType.NBT_DATA, data.data(), context.client().getNetworkHandler());
    }
}

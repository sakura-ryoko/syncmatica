package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.packet.ActorClientPlayNetworkHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.*;
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
 * canSend()
 * Wraps: canSend(payload.getId().id());
 * -> Wraps Internally as:
 * `--> ClientNetworkingImpl.getAddon(MinecraftClient.getInstance().getNetworkHandler()).getSendableChannels().contains(payload.getId().id());
 * send()
 * Wraps internally as:
 * --> MinecraftClient.getInstance().getNetworkHandler().sendPacket();
 */
public class ClientNetworkPlayHandler
{
    // Simple unified "sendPacket()" method
    // --> It doesn't care what payload/packet type it is.
    public static <T extends CustomPayload> void sendSyncPacket(T payload)
    {
        // Server-bound packet sent from the Client
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
            SyncLog.debug("ClientNetworkPlayHandler#sendSyncPacket(): [API] sending payload id: {}", payload.getId().id().toString());
        }
    }

    // Uses ClientPlayNetworkHandler method
    public static <T extends CustomPayload> void sendSyncPacket(T payload, ClientPlayNetworkHandler handler)
    {
        // Server-bound packet sent from the Client
        Packet<?> packet = new CustomPayloadC2SPacket(payload);
        if (handler.accepts(packet))
        {
            handler.sendPacket(packet);
            SyncLog.debug("ClientNetworkPlayHandler#sendSyncPacket(): [Handler] sending payload id: {}", payload.getId().id().toString());
        }
    }

    // --> ((SyncPacketClientHandler) SyncPacketClientHandler.getInstance()).receiveSyncmaticaPayload(payload.data(), ctx);

    // Client-bound packet sent from the Server
    public static void receiveSyncPacket(PacketType type, SyncByteBuf data, ClientPlayNetworkHandler handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        PacketByteBuf out = PayloadUtils.fromSyncBuf(data);
        SyncLog.debug("ClientNetworkPlayHandler#receiveSyncPacket(): received payload id: {}, size in bytes {}", type.getId().toString(), out.readableBytes());

        ActorClientPlayNetworkHandler.getInstance().packetEvent(type, out, handler, ci);
    }
    public static void receiveSyncNbt(PacketType type, NbtCompound data, ClientPlayNetworkHandler handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncNbt", false);
        SyncLog.debug("ClientNetworkPlayHandler#receiveSyncPacket(): received payload id: {}, size in bytes {}", type.getId().toString(), data.getSizeInBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveSyncPacket(): payload.readString(): {}", data.getString(SyncmaticaNbtData.KEY));

        ActorClientPlayNetworkHandler.getInstance().packetNbtEvent(type, data, handler, ci);
    }
    public static void receiveCancelShare(CancelShare data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_SHARE, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveConfirmUser(ConfirmUser data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CONFIRM_USER, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveFeature(Feature data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveFeatureRequest(FeatureRequest data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE_REQUEST, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveFinishedLitematic(FinishedLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FINISHED_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveMessage(Message data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MESSAGE, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModify(Modify data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyFinish(ModifyFinish data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_FINISH, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyRequest(ModifyRequest data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyRequestAccept(ModifyRequestAccept data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_ACCEPT, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveModifyRequestDeny(ModifyRequestDeny data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_DENY, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveReceivedLitematic(ReceivedLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.RECEIVED_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveRegisterMetadata(RegisterMetadata data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_METADATA, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveRegisterVersion(RegisterVersion data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_VERSION, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveRemoveSyncmatic(RemoveSyncmatic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REMOVE_SYNCMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }
    public static void receiveRequestDownload(RequestDownload data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REQUEST_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveSendLitematic(SendLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.SEND_LITEMATIC, data.byteBuf(), context.client().getNetworkHandler());
    }

    public static void receiveSyncNbtData(SyncmaticaNbtData data, ClientPlayNetworking.Context context)
    {
        receiveSyncNbt(PacketType.NBT_DATA, data.data(), context.client().getNetworkHandler());
    }
}

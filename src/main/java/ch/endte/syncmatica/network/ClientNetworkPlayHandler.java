package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

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
            SyncLog.debug("ClientNetworkPlayHandler#sendSyncPacket(): sending payload id: {}", payload.getId().id().toString());
        }
    }

    //((SyncPacketClientHandler) SyncPacketClientHandler.getInstance()).receiveSyncmaticaPayload(payload.data(), ctx);
    // Client-bound packet sent from the Server
    public static void receiveCancelShare(CancelShare data, ClientPlayNetworking.Context context)
    {
        SyncLog.debug("ClientNetworkPlayHandler#receiveCancelShare(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveCancelShare(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveConfirmUser(ConfirmUser data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveConfirmUser(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveConfirmUser(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveFeature(Feature data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveFeature(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveFeature(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveFeatureRequest(FeatureRequest data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveFeatureRequest(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveFeatureRequest(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveFinishedLitematic(FinishedLitematic data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveFinishedLitematic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveFinishedLitematic(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveMessage(Message data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveMessage(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveMessage(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModify(Modify data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveModify(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveModify(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyFinish(ModifyFinish data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyFinish(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyFinish(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyRequest(ModifyRequest data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyRequest(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyRequest(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyRequestAccept(ModifyRequestAccept data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyRequestAccept(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyRequestAccept(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyRequestDeny(ModifyRequestDeny data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyRequestDeny(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveModifyRequestDeny(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveReceivedLitematic(ReceivedLitematic data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveReceivedLitematic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveReceivedLitematic(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveRegisterMetadata(RegisterMetadata data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveRegisterMetadata(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveRegisterMetadata(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveRegisterVersion(RegisterVersion data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveRegisterVersion(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveRegisterVersion(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveRemoveSyncmatic(RemoveSyncmatic data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveRemoveSyncmatic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveRemoveSyncmatic(): payload.readString(): {}", data.byteBuf().readString(256));
    }
    public static void receiveRequestDownload(RequestDownload data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveRequestDownload(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveRequestDownload(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveSendLitematic(SendLitematic data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveSendLitematic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveSendLitematic(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveSyncNbtData(SyncmaticaNbtData data, ClientPlayNetworking.Context context) {
        SyncLog.debug("ClientNetworkPlayHandler#receiveSyncNbtData(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.data().getSizeInBytes());
        SyncLog.debug("ClientNetworkPlayHandler#receiveSyncNbtData(): payload.getString(): {}", data.data().getString("hello"));
    }
}

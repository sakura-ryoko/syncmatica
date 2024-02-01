package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * canSend()
 * Wraps: canSend(player.networkHandler, payload.getId().id());
 * --> Wraps Internally as:
 * `--> ServerNetworkingImpl.getAddon(player.networkHandler).getSendableChannels().contains(payload.getId().id());
 * send()
 * Wraps internally as:
 * --> player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(payload));
 */
public abstract class ServerNetworkPlayHandler
{
    // Simple unified "sendPacket()" method
    // --> It doesn't care what payload/packet type it is.
    public static <T extends CustomPayload> void sendSyncPacket(T payload, ServerPlayerEntity player)
    {
        // Client-bound packet sent by the Server
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            SyncLog.debug("ServerNetworkPlayHandler#sendSyncPacket(): sending payload id: {} to player: {}", payload.getId(), player.getName().getLiteralString());
        }
    }

    // Server-bound packet sent by a Client
    public static void receiveCancelShare(CancelShare data, ServerPlayNetworking.Context context)
    {
        SyncLog.debug("ServerNetworkPlayHandler#receiveCancelShare(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveCancelShare(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveConfirmUser(ConfirmUser data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveConfirmUser(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveConfirmUser(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveFeature(Feature data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveFeature(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveFeature(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveFeatureRequest(FeatureRequest data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveFeatureRequest(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveFeatureRequest(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveFinishedLitematic(FinishedLitematic data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveFinishedLitematic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveFinishedLitematic(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveMessage(Message data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveMessage(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveMessage(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModify(Modify data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveModify(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveModify(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyFinish(ModifyFinish data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyFinish(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyFinish(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyRequest(ModifyRequest data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyRequest(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyRequest(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyRequestAccept(ModifyRequestAccept data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyRequestAccept(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyRequestAccept(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveModifyRequestDeny(ModifyRequestDeny data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyRequestDeny(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveModifyRequestDeny(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveReceivedLitematic(ReceivedLitematic data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveReceivedLitematic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveReceivedLitematic(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveRegisterMetadata(RegisterMetadata data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveRegisterMetadata(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveRegisterMetadata(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveRegisterVersion(RegisterVersion data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveRegisterVersion(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveRegisterVersion(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveRemoveSyncmatic(RemoveSyncmatic data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveRemoveSyncmatic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveRemoveSyncmatic(): payload.readString(): {}", data.byteBuf().readString(256));
    }
    public static void receiveRequestDownload(RequestDownload data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveRequestDownload(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveRequestDownload(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveSendLitematic(SendLitematic data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveSendLitematic(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.byteBuf().readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveSendLitematic(): payload.readString(): {}", data.byteBuf().readString(256));
    }

    public static void receiveSyncNbtData(SyncmaticaNbtData data, ServerPlayNetworking.Context context) {
        SyncLog.debug("ServerNetworkPlayHandler#receiveSyncNbtData(): received payload id: {}, size in bytes {}", data.getId().id().toString(), data.data().getSizeInBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveSyncNbtData(): payload.getString(): {}", data.data().getString("hello"));
    }
}

package ch.endte.syncmatica.network;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworkPlayRegister
{
    static ServerPlayNetworking.PlayPayloadHandler<CancelShare> C2SSyncHandler_CancelShare;
    static ServerPlayNetworking.PlayPayloadHandler<ConfirmUser> C2SSyncHandler_ConfirmUser;
    static ServerPlayNetworking.PlayPayloadHandler<Feature> C2SSyncHandler_Feature;
    static ServerPlayNetworking.PlayPayloadHandler<FeatureRequest> C2SSyncHandler_FeatureRequest;
    static ServerPlayNetworking.PlayPayloadHandler<FinishedLitematic> C2SSyncHandler_FinishedLitematic;
    static ServerPlayNetworking.PlayPayloadHandler<Message> C2SSyncHandler_Message;
    static ServerPlayNetworking.PlayPayloadHandler<Modify> C2SSyncHandler_Modify;
    static ServerPlayNetworking.PlayPayloadHandler<ModifyFinish> C2SSyncHandler_ModifyFinish;
    static ServerPlayNetworking.PlayPayloadHandler<ModifyRequest> C2SSyncHandler_ModifyRequest;
    static ServerPlayNetworking.PlayPayloadHandler<ModifyRequestAccept> C2SSyncHandler_ModifyRequestAccept;
    static ServerPlayNetworking.PlayPayloadHandler<ModifyRequestDeny> C2SSyncHandler_ModifyRequestDeny;
    static ServerPlayNetworking.PlayPayloadHandler<ReceivedLitematic> C2SSyncHandler_ReceivedLitematic;
    static ServerPlayNetworking.PlayPayloadHandler<RegisterMetadata> C2SSyncHandler_RegisterMetadata;
    static ServerPlayNetworking.PlayPayloadHandler<RegisterVersion> C2SSyncHandler_RegisterVersion;
    static ServerPlayNetworking.PlayPayloadHandler<RemoveSyncmatic> C2SSyncHandler_RemoveSyncmatic;
    static ServerPlayNetworking.PlayPayloadHandler<RequestDownload> C2SSyncHandler_RequestDownload;
    static ServerPlayNetworking.PlayPayloadHandler<SendLitematic> C2SSyncHandler_SendLitematic;
    static ServerPlayNetworking.PlayPayloadHandler<SyncmaticaNbtData> C2SSyncHandler_NbtData;
    private static boolean receiversInit = false;
    public static void registerReceivers()
    {
        // Don't register more than once
        if (receiversInit)
            return;
        // Do when the server starts, not before
        if (SyncmaticaReference.isServer())
        {
            if (SyncmaticaReference.isDedicatedServer())
                SyncLog.debug("ServerHandlerManager#registerReceivers(): Game is running in Dedicated Server Mode.");
            SyncLog.debug("ServerHandlerManager#registerReceivers(): isServer() true -> registerSyncmaticaHandlers ...");
            ServerPlayNetworking.registerGlobalReceiver(CancelShare.TYPE, C2SSyncHandler_CancelShare);
            ServerPlayNetworking.registerGlobalReceiver(ConfirmUser.TYPE, C2SSyncHandler_ConfirmUser);
            ServerPlayNetworking.registerGlobalReceiver(Feature.TYPE, C2SSyncHandler_Feature);
            ServerPlayNetworking.registerGlobalReceiver(FeatureRequest.TYPE, C2SSyncHandler_FeatureRequest);
            ServerPlayNetworking.registerGlobalReceiver(FinishedLitematic.TYPE, C2SSyncHandler_FinishedLitematic);
            ServerPlayNetworking.registerGlobalReceiver(Message.TYPE, C2SSyncHandler_Message);
            ServerPlayNetworking.registerGlobalReceiver(Modify.TYPE, C2SSyncHandler_Modify);
            ServerPlayNetworking.registerGlobalReceiver(ModifyFinish.TYPE, C2SSyncHandler_ModifyFinish);
            ServerPlayNetworking.registerGlobalReceiver(ModifyRequest.TYPE, C2SSyncHandler_ModifyRequest);
            ServerPlayNetworking.registerGlobalReceiver(ModifyRequestAccept.TYPE, C2SSyncHandler_ModifyRequestAccept);
            ServerPlayNetworking.registerGlobalReceiver(ModifyRequestDeny.TYPE, C2SSyncHandler_ModifyRequestDeny);
            ServerPlayNetworking.registerGlobalReceiver(ReceivedLitematic.TYPE, C2SSyncHandler_ReceivedLitematic);
            ServerPlayNetworking.registerGlobalReceiver(RegisterMetadata.TYPE, C2SSyncHandler_RegisterMetadata);
            ServerPlayNetworking.registerGlobalReceiver(RegisterVersion.TYPE, C2SSyncHandler_RegisterVersion);
            ServerPlayNetworking.registerGlobalReceiver(RemoveSyncmatic.TYPE, C2SSyncHandler_RemoveSyncmatic);
            ServerPlayNetworking.registerGlobalReceiver(RequestDownload.TYPE, C2SSyncHandler_RequestDownload);
            ServerPlayNetworking.registerGlobalReceiver(SendLitematic.TYPE, C2SSyncHandler_SendLitematic);
            ServerPlayNetworking.registerGlobalReceiver(SyncmaticaNbtData.TYPE, C2SSyncHandler_NbtData);
            receiversInit = true;
        }
    }

    public static void unregisterReceivers()
    {
        // Do when server stops
        if (SyncmaticaReference.isServer())
        {
            SyncLog.debug("ServerHandlerManager#unregisterReceivers(): isServer() true -> unregisterSyncmaticaHandlers ...");
            ServerPlayNetworking.unregisterGlobalReceiver(CancelShare.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(ConfirmUser.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(Feature.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(FeatureRequest.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(FinishedLitematic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(Message.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(Modify.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(ModifyFinish.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(ModifyRequest.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(ModifyRequestAccept.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(ModifyRequestDeny.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(ReceivedLitematic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(RegisterMetadata.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(RegisterVersion.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(RemoveSyncmatic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(RequestDownload.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SendLitematic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncmaticaNbtData.TYPE.id());
            receiversInit = false;
        }
    }
    static
    {
        C2SSyncHandler_CancelShare = ServerNetworkPlayHandler::receiveCancelShare;
        C2SSyncHandler_ConfirmUser = ServerNetworkPlayHandler::receiveConfirmUser;
        C2SSyncHandler_Feature = ServerNetworkPlayHandler::receiveFeature;
        C2SSyncHandler_FeatureRequest = ServerNetworkPlayHandler::receiveFeatureRequest;
        C2SSyncHandler_FinishedLitematic = ServerNetworkPlayHandler::receiveFinishedLitematic;
        C2SSyncHandler_Message = ServerNetworkPlayHandler::receiveMessage;
        C2SSyncHandler_Modify = ServerNetworkPlayHandler::receiveModify;
        C2SSyncHandler_ModifyFinish = ServerNetworkPlayHandler::receiveModifyFinish;
        C2SSyncHandler_ModifyRequest = ServerNetworkPlayHandler::receiveModifyRequest;
        C2SSyncHandler_ModifyRequestAccept = ServerNetworkPlayHandler::receiveModifyRequestAccept;
        C2SSyncHandler_ModifyRequestDeny = ServerNetworkPlayHandler::receiveModifyRequestDeny;
        C2SSyncHandler_ReceivedLitematic = ServerNetworkPlayHandler::receiveReceivedLitematic;
        C2SSyncHandler_RegisterMetadata = ServerNetworkPlayHandler::receiveRegisterMetadata;
        C2SSyncHandler_RegisterVersion = ServerNetworkPlayHandler::receiveRegisterVersion;
        C2SSyncHandler_RemoveSyncmatic = ServerNetworkPlayHandler::receiveRemoveSyncmatic;
        C2SSyncHandler_RequestDownload = ServerNetworkPlayHandler::receiveRequestDownload;
        C2SSyncHandler_SendLitematic = ServerNetworkPlayHandler::receiveSendLitematic;
        C2SSyncHandler_NbtData = ServerNetworkPlayHandler::receiveSyncNbtData;
    }
}

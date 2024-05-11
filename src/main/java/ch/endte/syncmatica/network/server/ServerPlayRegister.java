package ch.endte.syncmatica.network.server;

import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.channels.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Registers Fabric API Networking payload handlers -- They have to be mapped per a Payload Type / Context
 */
public class ServerPlayRegister
{
    static ServerPlayNetworking.PlayPayloadHandler<SyncCancelShare> C2SSyncHandler_CancelShare;
    static ServerPlayNetworking.PlayPayloadHandler<SyncCancelLitematic> C2SSyncHandler_CancelLitematic;
    static ServerPlayNetworking.PlayPayloadHandler<SyncConfirmUser> C2SSyncHandler_ConfirmUser;
    static ServerPlayNetworking.PlayPayloadHandler<SyncFeature> C2SSyncHandler_Feature;
    static ServerPlayNetworking.PlayPayloadHandler<SyncFeatureRequest> C2SSyncHandler_FeatureRequest;
    static ServerPlayNetworking.PlayPayloadHandler<SyncFinishedLitematic> C2SSyncHandler_FinishedLitematic;
    static ServerPlayNetworking.PlayPayloadHandler<SyncMessage> C2SSyncHandler_Message;
    static ServerPlayNetworking.PlayPayloadHandler<SyncModify> C2SSyncHandler_Modify;
    static ServerPlayNetworking.PlayPayloadHandler<SyncModifyFinish> C2SSyncHandler_ModifyFinish;
    static ServerPlayNetworking.PlayPayloadHandler<SyncModifyRequest> C2SSyncHandler_ModifyRequest;
    static ServerPlayNetworking.PlayPayloadHandler<SyncModifyRequestAccept> C2SSyncHandler_ModifyRequestAccept;
    static ServerPlayNetworking.PlayPayloadHandler<SyncModifyRequestDeny> C2SSyncHandler_ModifyRequestDeny;
    static ServerPlayNetworking.PlayPayloadHandler<SyncReceivedLitematic> C2SSyncHandler_ReceivedLitematic;
    static ServerPlayNetworking.PlayPayloadHandler<SyncRegisterMetadata> C2SSyncHandler_RegisterMetadata;
    static ServerPlayNetworking.PlayPayloadHandler<SyncRegisterVersion> C2SSyncHandler_RegisterVersion;
    static ServerPlayNetworking.PlayPayloadHandler<SyncRemoveSyncmatic> C2SSyncHandler_RemoveSyncmatic;
    static ServerPlayNetworking.PlayPayloadHandler<SyncRequestDownload> C2SSyncHandler_RequestDownload;
    static ServerPlayNetworking.PlayPayloadHandler<SyncSendLitematic> C2SSyncHandler_SendLitematic;
    private static boolean receiversInit = false;

    public static void registerReceivers()
    {
        // Don't register more than once
        if (receiversInit)
            return;
        if (Reference.isServer() || Reference.isIntegratedServer() || Reference.isOpenToLan())
        {
            Syncmatica.debug("ServerPlayRegister#registerReceivers(): [SERVER] -> registerSyncmaticaHandlers");
            ServerPlayNetworking.registerGlobalReceiver(SyncCancelShare.TYPE, C2SSyncHandler_CancelShare);
            ServerPlayNetworking.registerGlobalReceiver(SyncCancelLitematic.TYPE, C2SSyncHandler_CancelLitematic);
            ServerPlayNetworking.registerGlobalReceiver(SyncConfirmUser.TYPE, C2SSyncHandler_ConfirmUser);
            ServerPlayNetworking.registerGlobalReceiver(SyncFeature.TYPE, C2SSyncHandler_Feature);
            ServerPlayNetworking.registerGlobalReceiver(SyncFeatureRequest.TYPE, C2SSyncHandler_FeatureRequest);
            ServerPlayNetworking.registerGlobalReceiver(SyncFinishedLitematic.TYPE, C2SSyncHandler_FinishedLitematic);
            ServerPlayNetworking.registerGlobalReceiver(SyncMessage.TYPE, C2SSyncHandler_Message);
            ServerPlayNetworking.registerGlobalReceiver(SyncModify.TYPE, C2SSyncHandler_Modify);
            ServerPlayNetworking.registerGlobalReceiver(SyncModifyFinish.TYPE, C2SSyncHandler_ModifyFinish);
            ServerPlayNetworking.registerGlobalReceiver(SyncModifyRequest.TYPE, C2SSyncHandler_ModifyRequest);
            ServerPlayNetworking.registerGlobalReceiver(SyncModifyRequestAccept.TYPE, C2SSyncHandler_ModifyRequestAccept);
            ServerPlayNetworking.registerGlobalReceiver(SyncModifyRequestDeny.TYPE, C2SSyncHandler_ModifyRequestDeny);
            ServerPlayNetworking.registerGlobalReceiver(SyncReceivedLitematic.TYPE, C2SSyncHandler_ReceivedLitematic);
            ServerPlayNetworking.registerGlobalReceiver(SyncRegisterMetadata.TYPE, C2SSyncHandler_RegisterMetadata);
            ServerPlayNetworking.registerGlobalReceiver(SyncRegisterVersion.TYPE, C2SSyncHandler_RegisterVersion);
            ServerPlayNetworking.registerGlobalReceiver(SyncRemoveSyncmatic.TYPE, C2SSyncHandler_RemoveSyncmatic);
            ServerPlayNetworking.registerGlobalReceiver(SyncRequestDownload.TYPE, C2SSyncHandler_RequestDownload);
            ServerPlayNetworking.registerGlobalReceiver(SyncSendLitematic.TYPE, C2SSyncHandler_SendLitematic);
            receiversInit = true;
        }
    }

    public static void unregisterReceivers()
    {
        if (Reference.isServer() || Reference.isIntegratedServer() || Reference.isOpenToLan())
        {
            Syncmatica.debug("ServerPlayRegister#unregisterReceivers(): [SERVER] -> unregisterSyncmaticaHandlers");
            ServerPlayNetworking.unregisterGlobalReceiver(SyncCancelShare.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncCancelLitematic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncConfirmUser.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncFeature.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncFeatureRequest.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncFinishedLitematic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncMessage.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncModify.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncModifyFinish.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncModifyRequest.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncModifyRequestAccept.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncModifyRequestDeny.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncReceivedLitematic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncRegisterMetadata.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncRegisterVersion.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncRemoveSyncmatic.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncRequestDownload.TYPE.id());
            ServerPlayNetworking.unregisterGlobalReceiver(SyncSendLitematic.TYPE.id());
            receiversInit = false;
        }
    }

    static
    {
        C2SSyncHandler_CancelShare = ServerPlayHandler::receiveCancelShare;
        C2SSyncHandler_CancelLitematic = ServerPlayHandler::receiveCancelLitematic;
        C2SSyncHandler_ConfirmUser = ServerPlayHandler::receiveConfirmUser;
        C2SSyncHandler_Feature = ServerPlayHandler::receiveFeature;
        C2SSyncHandler_FeatureRequest = ServerPlayHandler::receiveFeatureRequest;
        C2SSyncHandler_FinishedLitematic = ServerPlayHandler::receiveFinishedLitematic;
        C2SSyncHandler_Message = ServerPlayHandler::receiveMessage;
        C2SSyncHandler_Modify = ServerPlayHandler::receiveModify;
        C2SSyncHandler_ModifyFinish = ServerPlayHandler::receiveModifyFinish;
        C2SSyncHandler_ModifyRequest = ServerPlayHandler::receiveModifyRequest;
        C2SSyncHandler_ModifyRequestAccept = ServerPlayHandler::receiveModifyRequestAccept;
        C2SSyncHandler_ModifyRequestDeny = ServerPlayHandler::receiveModifyRequestDeny;
        C2SSyncHandler_ReceivedLitematic = ServerPlayHandler::receiveReceivedLitematic;
        C2SSyncHandler_RegisterMetadata = ServerPlayHandler::receiveRegisterMetadata;
        C2SSyncHandler_RegisterVersion = ServerPlayHandler::receiveRegisterVersion;
        C2SSyncHandler_RemoveSyncmatic = ServerPlayHandler::receiveRemoveSyncmatic;
        C2SSyncHandler_RequestDownload = ServerPlayHandler::receiveRequestDownload;
        C2SSyncHandler_SendLitematic = ServerPlayHandler::receiveSendLitematic;
    }
}

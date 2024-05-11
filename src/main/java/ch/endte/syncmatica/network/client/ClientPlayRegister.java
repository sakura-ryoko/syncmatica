package ch.endte.syncmatica.network.client;

import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.channels.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Registers Fabric API Networking payload handlers -- They have to be mapped per a Payload Type / Context
 */
public class ClientPlayRegister
{
    static ClientPlayNetworking.PlayPayloadHandler<SyncCancelShare> S2CSyncHandler_CancelShare;
    static ClientPlayNetworking.PlayPayloadHandler<SyncCancelLitematic> S2CSyncHandler_CancelLitematic;
    static ClientPlayNetworking.PlayPayloadHandler<SyncConfirmUser> S2CSyncHandler_ConfirmUser;
    static ClientPlayNetworking.PlayPayloadHandler<SyncFeature> S2CSyncHandler_Feature;
    static ClientPlayNetworking.PlayPayloadHandler<SyncFeatureRequest> S2CSyncHandler_FeatureRequest;
    static ClientPlayNetworking.PlayPayloadHandler<SyncFinishedLitematic> S2CSyncHandler_FinishedLitematic;
    static ClientPlayNetworking.PlayPayloadHandler<SyncMessage> S2CSyncHandler_Message;
    static ClientPlayNetworking.PlayPayloadHandler<SyncModify> S2CSyncHandler_Modify;
    static ClientPlayNetworking.PlayPayloadHandler<SyncModifyFinish> S2CSyncHandler_ModifyFinish;
    static ClientPlayNetworking.PlayPayloadHandler<SyncModifyRequest> S2CSyncHandler_ModifyRequest;
    static ClientPlayNetworking.PlayPayloadHandler<SyncModifyRequestAccept> S2CSyncHandler_ModifyRequestAccept;
    static ClientPlayNetworking.PlayPayloadHandler<SyncModifyRequestDeny> S2CSyncHandler_ModifyRequestDeny;
    static ClientPlayNetworking.PlayPayloadHandler<SyncReceivedLitematic> S2CSyncHandler_ReceivedLitematic;
    static ClientPlayNetworking.PlayPayloadHandler<SyncRegisterMetadata> S2CSyncHandler_RegisterMetadata;
    static ClientPlayNetworking.PlayPayloadHandler<SyncRegisterVersion> S2CSyncHandler_RegisterVersion;
    static ClientPlayNetworking.PlayPayloadHandler<SyncRemoveSyncmatic> S2CSyncHandler_RemoveSyncmatic;
    static ClientPlayNetworking.PlayPayloadHandler<SyncRequestDownload> S2CSyncHandler_RequestDownload;
    static ClientPlayNetworking.PlayPayloadHandler<SyncSendLitematic> S2CSyncHandler_SendLitematic;
    private static boolean receiversInit = false;

    public static void registerReceivers()
    {
        // Don't register more than once
        if (receiversInit)
            return;
        if (Reference.isClient())
        {
            Syncmatica.debug("ClientPlayRegister#registerReceivers(): [CLIENT] -> registerSyncmaticaHandlers");
            ClientPlayNetworking.registerGlobalReceiver(SyncCancelShare.TYPE, S2CSyncHandler_CancelShare);
            ClientPlayNetworking.registerGlobalReceiver(SyncCancelLitematic.TYPE, S2CSyncHandler_CancelLitematic);
            ClientPlayNetworking.registerGlobalReceiver(SyncConfirmUser.TYPE, S2CSyncHandler_ConfirmUser);
            ClientPlayNetworking.registerGlobalReceiver(SyncFeature.TYPE, S2CSyncHandler_Feature);
            ClientPlayNetworking.registerGlobalReceiver(SyncFeatureRequest.TYPE, S2CSyncHandler_FeatureRequest);
            ClientPlayNetworking.registerGlobalReceiver(SyncFinishedLitematic.TYPE, S2CSyncHandler_FinishedLitematic);
            ClientPlayNetworking.registerGlobalReceiver(SyncMessage.TYPE, S2CSyncHandler_Message);
            ClientPlayNetworking.registerGlobalReceiver(SyncModify.TYPE, S2CSyncHandler_Modify);
            ClientPlayNetworking.registerGlobalReceiver(SyncModifyFinish.TYPE, S2CSyncHandler_ModifyFinish);
            ClientPlayNetworking.registerGlobalReceiver(SyncModifyRequest.TYPE, S2CSyncHandler_ModifyRequest);
            ClientPlayNetworking.registerGlobalReceiver(SyncModifyRequestAccept.TYPE, S2CSyncHandler_ModifyRequestAccept);
            ClientPlayNetworking.registerGlobalReceiver(SyncModifyRequestDeny.TYPE, S2CSyncHandler_ModifyRequestDeny);
            ClientPlayNetworking.registerGlobalReceiver(SyncReceivedLitematic.TYPE, S2CSyncHandler_ReceivedLitematic);
            ClientPlayNetworking.registerGlobalReceiver(SyncRegisterMetadata.TYPE, S2CSyncHandler_RegisterMetadata);
            ClientPlayNetworking.registerGlobalReceiver(SyncRegisterVersion.TYPE, S2CSyncHandler_RegisterVersion);
            ClientPlayNetworking.registerGlobalReceiver(SyncRemoveSyncmatic.TYPE, S2CSyncHandler_RemoveSyncmatic);
            ClientPlayNetworking.registerGlobalReceiver(SyncRequestDownload.TYPE, S2CSyncHandler_RequestDownload);
            ClientPlayNetworking.registerGlobalReceiver(SyncSendLitematic.TYPE, S2CSyncHandler_SendLitematic);
            receiversInit = true;
        }
    }

    public static void unregisterReceivers()
    {
        if (Reference.isClient())
        {
            Syncmatica.debug("ClientPlayRegister#unregisterReceivers(): [CLIENT] -> unregisterSyncmaticaHandlers");
            ClientPlayNetworking.unregisterGlobalReceiver(SyncCancelShare.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncCancelLitematic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncConfirmUser.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncFeature.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncFeatureRequest.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncFinishedLitematic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncMessage.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncModify.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncModifyFinish.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncModifyRequest.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncModifyRequestAccept.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncModifyRequestDeny.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncReceivedLitematic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncRegisterMetadata.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncRegisterVersion.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncRemoveSyncmatic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncRequestDownload.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncSendLitematic.TYPE.id());
            receiversInit = false;
        }
    }

    static
    {
        S2CSyncHandler_CancelShare = ClientPlayHandler::receiveCancelShare;
        S2CSyncHandler_CancelLitematic = ClientPlayHandler::receiveCancelLitematic;
        S2CSyncHandler_ConfirmUser = ClientPlayHandler::receiveConfirmUser;
        S2CSyncHandler_Feature = ClientPlayHandler::receiveFeature;
        S2CSyncHandler_FeatureRequest = ClientPlayHandler::receiveFeatureRequest;
        S2CSyncHandler_FinishedLitematic = ClientPlayHandler::receiveFinishedLitematic;
        S2CSyncHandler_Message = ClientPlayHandler::receiveMessage;
        S2CSyncHandler_Modify = ClientPlayHandler::receiveModify;
        S2CSyncHandler_ModifyFinish = ClientPlayHandler::receiveModifyFinish;
        S2CSyncHandler_ModifyRequest = ClientPlayHandler::receiveModifyRequest;
        S2CSyncHandler_ModifyRequestAccept = ClientPlayHandler::receiveModifyRequestAccept;
        S2CSyncHandler_ModifyRequestDeny = ClientPlayHandler::receiveModifyRequestDeny;
        S2CSyncHandler_ReceivedLitematic = ClientPlayHandler::receiveReceivedLitematic;
        S2CSyncHandler_RegisterMetadata = ClientPlayHandler::receiveRegisterMetadata;
        S2CSyncHandler_RegisterVersion = ClientPlayHandler::receiveRegisterVersion;
        S2CSyncHandler_RemoveSyncmatic = ClientPlayHandler::receiveRemoveSyncmatic;
        S2CSyncHandler_RequestDownload = ClientPlayHandler::receiveRequestDownload;
        S2CSyncHandler_SendLitematic = ClientPlayHandler::receiveSendLitematic;
    }
}

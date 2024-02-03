package ch.endte.syncmatica.network;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Registers Fabric API Networking payload handlers -- They have to be mapped per a Payload Type / Context
 */
public class ClientNetworkPlayRegister
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
    static ClientPlayNetworking.PlayPayloadHandler<SyncNbtData> S2CSyncHandler_NbtData;
    private static boolean receiversInit = false;
    public static void registerReceivers()
    {
        // Don't register more than once
        if (receiversInit)
            return;
        // Wait until world/server joined
        if (SyncmaticaReference.isClient())
        {
            if (SyncmaticaReference.isSinglePlayer())
                SyncLog.debug("ClientHandlerManager#registerReceivers(): Game is running in Single Player Mode.");
            SyncLog.debug("ClientHandlerManager#registerReceivers(): isClient() true -> registerSyncmaticaHandlers ...");
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
            ClientPlayNetworking.registerGlobalReceiver(SyncNbtData.TYPE, S2CSyncHandler_NbtData);
            receiversInit = true;
        }
    }

    public static void unregisterReceivers()
    {
        // Do when disconnecting from server/world
        if (SyncmaticaReference.isClient())
        {
            SyncLog.debug("ClientHandlerManager#unregisterDefaultReceivers(): isClient() true -> unregisterSyncmaticaHandlers ...");
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
            ClientPlayNetworking.unregisterGlobalReceiver(SyncNbtData.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncNbtData.TYPE.id());
            receiversInit = false;
        }
    }
    static
    {
        S2CSyncHandler_CancelShare = ClientNetworkPlayHandler::receiveCancelShare;
        S2CSyncHandler_CancelLitematic = ClientNetworkPlayHandler::receiveCancelLitematic;
        S2CSyncHandler_ConfirmUser = ClientNetworkPlayHandler::receiveConfirmUser;
        S2CSyncHandler_Feature = ClientNetworkPlayHandler::receiveFeature;
        S2CSyncHandler_FeatureRequest = ClientNetworkPlayHandler::receiveFeatureRequest;
        S2CSyncHandler_FinishedLitematic = ClientNetworkPlayHandler::receiveFinishedLitematic;
        S2CSyncHandler_Message = ClientNetworkPlayHandler::receiveMessage;
        S2CSyncHandler_Modify = ClientNetworkPlayHandler::receiveModify;
        S2CSyncHandler_ModifyFinish = ClientNetworkPlayHandler::receiveModifyFinish;
        S2CSyncHandler_ModifyRequest = ClientNetworkPlayHandler::receiveModifyRequest;
        S2CSyncHandler_ModifyRequestAccept = ClientNetworkPlayHandler::receiveModifyRequestAccept;
        S2CSyncHandler_ModifyRequestDeny = ClientNetworkPlayHandler::receiveModifyRequestDeny;
        S2CSyncHandler_ReceivedLitematic = ClientNetworkPlayHandler::receiveReceivedLitematic;
        S2CSyncHandler_RegisterMetadata = ClientNetworkPlayHandler::receiveRegisterMetadata;
        S2CSyncHandler_RegisterVersion = ClientNetworkPlayHandler::receiveRegisterVersion;
        S2CSyncHandler_RemoveSyncmatic = ClientNetworkPlayHandler::receiveRemoveSyncmatic;
        S2CSyncHandler_RequestDownload = ClientNetworkPlayHandler::receiveRequestDownload;
        S2CSyncHandler_SendLitematic = ClientNetworkPlayHandler::receiveSendLitematic;
        S2CSyncHandler_NbtData = ClientNetworkPlayHandler::receiveSyncNbtData;
    }
}

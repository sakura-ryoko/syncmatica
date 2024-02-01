package ch.endte.syncmatica.network;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkPlayRegister
{
    static ClientPlayNetworking.PlayPayloadHandler<CancelShare> S2CSyncHandler_CancelShare;
    static ClientPlayNetworking.PlayPayloadHandler<ConfirmUser> S2CSyncHandler_ConfirmUser;
    static ClientPlayNetworking.PlayPayloadHandler<Feature> S2CSyncHandler_Feature;
    static ClientPlayNetworking.PlayPayloadHandler<FeatureRequest> S2CSyncHandler_FeatureRequest;
    static ClientPlayNetworking.PlayPayloadHandler<FinishedLitematic> S2CSyncHandler_FinishedLitematic;
    static ClientPlayNetworking.PlayPayloadHandler<Message> S2CSyncHandler_Message;
    static ClientPlayNetworking.PlayPayloadHandler<Modify> S2CSyncHandler_Modify;
    static ClientPlayNetworking.PlayPayloadHandler<ModifyFinish> S2CSyncHandler_ModifyFinish;
    static ClientPlayNetworking.PlayPayloadHandler<ModifyRequest> S2CSyncHandler_ModifyRequest;
    static ClientPlayNetworking.PlayPayloadHandler<ModifyRequestAccept> S2CSyncHandler_ModifyRequestAccept;
    static ClientPlayNetworking.PlayPayloadHandler<ModifyRequestDeny> S2CSyncHandler_ModifyRequestDeny;
    static ClientPlayNetworking.PlayPayloadHandler<ReceivedLitematic> S2CSyncHandler_ReceivedLitematic;
    static ClientPlayNetworking.PlayPayloadHandler<RegisterMetadata> S2CSyncHandler_RegisterMetadata;
    static ClientPlayNetworking.PlayPayloadHandler<RegisterVersion> S2CSyncHandler_RegisterVersion;
    static ClientPlayNetworking.PlayPayloadHandler<RemoveSyncmatic> S2CSyncHandler_RemoveSyncmatic;
    static ClientPlayNetworking.PlayPayloadHandler<RequestDownload> S2CSyncHandler_RequestDownload;
    static ClientPlayNetworking.PlayPayloadHandler<SendLitematic> S2CSyncHandler_SendLitematic;
    static ClientPlayNetworking.PlayPayloadHandler<SyncmaticaNbtData> S2CSyncHandler_NbtData;
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
            ClientPlayNetworking.registerGlobalReceiver(CancelShare.TYPE, S2CSyncHandler_CancelShare);
            ClientPlayNetworking.registerGlobalReceiver(ConfirmUser.TYPE, S2CSyncHandler_ConfirmUser);
            ClientPlayNetworking.registerGlobalReceiver(Feature.TYPE, S2CSyncHandler_Feature);
            ClientPlayNetworking.registerGlobalReceiver(FeatureRequest.TYPE, S2CSyncHandler_FeatureRequest);
            ClientPlayNetworking.registerGlobalReceiver(FinishedLitematic.TYPE, S2CSyncHandler_FinishedLitematic);
            ClientPlayNetworking.registerGlobalReceiver(Message.TYPE, S2CSyncHandler_Message);
            ClientPlayNetworking.registerGlobalReceiver(Modify.TYPE, S2CSyncHandler_Modify);
            ClientPlayNetworking.registerGlobalReceiver(ModifyFinish.TYPE, S2CSyncHandler_ModifyFinish);
            ClientPlayNetworking.registerGlobalReceiver(ModifyRequest.TYPE, S2CSyncHandler_ModifyRequest);
            ClientPlayNetworking.registerGlobalReceiver(ModifyRequestAccept.TYPE, S2CSyncHandler_ModifyRequestAccept);
            ClientPlayNetworking.registerGlobalReceiver(ModifyRequestDeny.TYPE, S2CSyncHandler_ModifyRequestDeny);
            ClientPlayNetworking.registerGlobalReceiver(ReceivedLitematic.TYPE, S2CSyncHandler_ReceivedLitematic);
            ClientPlayNetworking.registerGlobalReceiver(RegisterMetadata.TYPE, S2CSyncHandler_RegisterMetadata);
            ClientPlayNetworking.registerGlobalReceiver(RegisterVersion.TYPE, S2CSyncHandler_RegisterVersion);
            ClientPlayNetworking.registerGlobalReceiver(RemoveSyncmatic.TYPE, S2CSyncHandler_RemoveSyncmatic);
            ClientPlayNetworking.registerGlobalReceiver(RequestDownload.TYPE, S2CSyncHandler_RequestDownload);
            ClientPlayNetworking.registerGlobalReceiver(SendLitematic.TYPE, S2CSyncHandler_SendLitematic);
            ClientPlayNetworking.registerGlobalReceiver(SyncmaticaNbtData.TYPE, S2CSyncHandler_NbtData);
            receiversInit = true;
        }
    }

    public static void unregisterReceivers()
    {
        // Do when disconnecting from server/world
        if (SyncmaticaReference.isClient())
        {
            SyncLog.debug("ClientHandlerManager#unregisterDefaultReceivers(): isClient() true -> unregisterSyncmaticaHandlers ...");
            ClientPlayNetworking.unregisterGlobalReceiver(CancelShare.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(ConfirmUser.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(Feature.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(FeatureRequest.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(FinishedLitematic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(Message.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(Modify.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(ModifyFinish.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(ModifyRequest.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(ModifyRequestAccept.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(ModifyRequestDeny.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(ReceivedLitematic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(RegisterMetadata.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(RegisterVersion.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(RemoveSyncmatic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(RequestDownload.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SendLitematic.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncmaticaNbtData.TYPE.id());
            ClientPlayNetworking.unregisterGlobalReceiver(SyncmaticaNbtData.TYPE.id());
            receiversInit = false;
        }
    }
    static
    {
        S2CSyncHandler_CancelShare = ClientNetworkPlayHandler::receiveCancelShare;
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

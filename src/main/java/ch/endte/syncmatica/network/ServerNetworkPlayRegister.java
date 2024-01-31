package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworkPlayRegister
{
    static ServerPlayNetworking.PlayPayloadHandler<SyncmaticaPayload> C2SSyncmaticaHandler;
    
    public static void registerReceivers()
    {
        // Do when the server starts, not before
        if (SyncmaticaReference.isServer())
        {
            SyncLog.debug("ServerHandlerManager#registerReceivers(): isServer() true.");
            SyncLog.debug("ServerHandlerManager#registerReceivers(): registerSyncmaticaHandler()");
            ServerPlayNetworking.registerGlobalReceiver(SyncmaticaPayload.TYPE, C2SSyncmaticaHandler);
        }
    }

    public static void unregisterReceivers()
    {
        // Do when server stops
        if (SyncmaticaReference.isServer())
        {
            SyncLog.debug("ServerHandlerManager#unregisterReceivers(): isServer() true.");
            SyncLog.debug("ServerHandlerManager#unregisterReceivers(): unregisterSyncmaticaHandler()");
            ServerPlayNetworking.unregisterGlobalReceiver(SyncmaticaPayload.TYPE.id());
        }
    }
    static
    {
        C2SSyncmaticaHandler = ServerNetworkPlayHandler::receiveSyncmaticaServerPayload;
    }
}
